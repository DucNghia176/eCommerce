package ecommerce.orderservice.service.impl;

import ecommerce.apicommon1.config.TokenInfo;
import ecommerce.apicommon1.kafka.event.InventoryKafkaEvent;
import ecommerce.apicommon1.kafka.event.PaymentKafkaEvent;
import ecommerce.apicommon1.model.request.UpdateOrderStatusRequest;
import ecommerce.apicommon1.model.response.*;
import ecommerce.apicommon1.model.status.OrderStatus;
import ecommerce.apicommon1.model.status.PaymentMethodStatus;
import ecommerce.apicommon1.model.status.PaymentStatus;
import ecommerce.orderservice.client.CartClient;
import ecommerce.orderservice.client.PaymentClient;
import ecommerce.orderservice.client.ProductClient;
import ecommerce.orderservice.client.UserClient;
import ecommerce.orderservice.dto.request.OrderCreateRequest;
import ecommerce.orderservice.dto.request.OrderItemRequest;
import ecommerce.orderservice.dto.request.OrderRequest;
import ecommerce.orderservice.dto.response.OrderCreateResponse;
import ecommerce.orderservice.dto.response.OrderQuantityResponse;
import ecommerce.orderservice.dto.response.OrderResponse;
import ecommerce.orderservice.dto.response.OrdersAD;
import ecommerce.orderservice.entity.OrderDetail;
import ecommerce.orderservice.entity.Orders;
import ecommerce.orderservice.kafka.KafkaOrder;
import ecommerce.orderservice.mapper.OrderMapper;
import ecommerce.orderservice.repository.OrderDetailRepository;
import ecommerce.orderservice.repository.OrderRepository;
import ecommerce.orderservice.service.OrderService;
import ecommerce.orderservice.util.GenerateKey;
import ecommerce.orderservice.util.OrderStatusUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Map<PaymentMethodStatus, OrderStatus> PAYMENT_FLOW = Map.of(
            PaymentMethodStatus.COD, OrderStatus.CONFIRMED,      // COD: xác nhận ngay
            PaymentMethodStatus.PAYPAL, OrderStatus.PENDING      //PayPal: chờ xác nhận
    );
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final KafkaOrder kafkaOrder;
    private final CartClient cartClient;
    private final TokenInfo tokenInfo;
    private final OrderDetailRepository orderDetailRepository;
    private final KafkaTemplate<String, InventoryKafkaEvent> inventoryKafka;
    private final KafkaTemplate<String, PaymentKafkaEvent> paymentKafka;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final PaymentClient paymentClient;
    private final Executor contextAwareExecutor;
    private final OrderStatusUtil orderStatusUtil;
    private final GenerateKey generateKey;

    private String generateOrderCode() {
        String prefix = "ORD";
        String dateTimePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        int random = (int) (Math.random() * 900) + 100;
        return prefix + "-" + dateTimePart + "-" + random;
    }

    @Override
    @Transactional
    public ApiResponse<OrderResponse> placeOrder(OrderRequest request) {
        try {
            Long userId = tokenInfo.getUserId();

            ApiResponse<List<CartItemResponse>> cartResponse = cartClient.getSelectedCartItem(request.getSelectedCartItemIds());
            List<CartItemResponse> cart = cartResponse.getData();

            if (cart == null) {
                return ApiResponse.<OrderResponse>builder()
                        .code(400)
                        .message("Không có sản phẩm nào được chọn trong giỏ hàng")
                        .data(null)
                        .build();
            }

            // 2. Tính tổng tiền
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CartItemResponse item : cart) {
                BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }

            // 3. Tạo đối tượng Orders từ request + mapper
            Orders order = orderMapper.toOrderEntity(request);
            order.setUserId(userId);// gán userId từ cart
            order.setTotalAmount(totalAmount);
            order.setPaymentMethod(request.getPaymentMethod());
            order.setOrderCode(generateOrderCode());
            order.setCreatedAt(LocalDateTime.now());

            // 4. Lưu tạm order để có ID
            orderRepository.save(order);

            // 5. Tạo danh sách UserOrderDetailResponse từ CartItemResponse
            List<OrderDetail> details = cart.stream().map(item ->
                    OrderDetail.builder()
                            .order(order)
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .createdAt(LocalDateTime.now())
                            .build()
            ).toList();

            orderDetailRepository.saveAll(details);
            order.setOrderDetails(details);

            // 6. Gửi Kafka thông tin thanh toán
            PaymentKafkaEvent payment = PaymentKafkaEvent.builder()
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .orderCode(order.getOrderCode())
                    .totalAmount(order.getTotalAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .timestamp(LocalDateTime.now())
                    .build();
            paymentKafka.send("payment-topic", payment);

            List<InventoryKafkaEvent> inventory = cart.stream().map(item -> {
                // Gọi product-service để lấy skuCode từ productId
                String skuCode = productClient.getSkuCode(item.getProductId());

                return InventoryKafkaEvent.builder()
                        .skuCode(skuCode)
                        .quantity(item.getQuantity())
                        .build();
            }).toList();

            for (InventoryKafkaEvent event : inventory) {
                inventoryKafka.send("place-order", event);
            }


            List<Long> itemId = cart.stream().map(CartItemResponse::getId).toList();
            // 8. Xóa giỏ hàng đã chọn
            cartClient.clearSelectedCartItems(itemId);

            OrderResponse response = orderMapper.toResponse(order);
            // Gửi thông báo sang Kafka
            kafkaOrder.sendMessage("order-events", "Tạo đơn hàng thành công: ID " + order.getId());
            return ApiResponse.<OrderResponse>builder()
                    .code(200)
                    .message("Tạo đơn hàng thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            kafkaOrder.sendMessage("order-events", "Tạo đơn hàng thất bại");
            return ApiResponse.<OrderResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();

        }
    }

    @Override
    @Transactional
    public OrderCreateResponse order(OrderCreateRequest request) {
        Long userId = tokenInfo.getUserId();

        if (request.getPaymentMethod() == null) {
            request.setPaymentMethod(PaymentMethodStatus.COD);
        }

        String cartSignature = generateKey.generateCartSignature(request.getItems());

        Optional<Orders> existingOrder = orderRepository.findByUserIdAndCartSignature(userId, cartSignature);
        if (existingOrder.isPresent()) {
            return orderMapper.toDto(existingOrder.get()); // trả về order cũ
        }

        //lưu tạm order
        Orders order = Orders.builder()
                .userId(userId)
                .shippingAddress(request.getShippingAddress())
                .status(PAYMENT_FLOW.getOrDefault(request.getPaymentMethod(), OrderStatus.PENDING))
                .paymentMethod(request.getPaymentMethod())
                .isActive(1)
                .createdAt(LocalDateTime.now())
                .note(request.getNote())
                .cartSignature(cartSignature)
                .build();
        orderRepository.save(order);

        //nếu đặt hàng từ cart
        if (request.isFromCart()) {
            ApiResponse<CartResponse> cartRes = cartClient.getCartByUserId();
            List<CartItemResponse> cartItem = cartRes.getData().getItems();

            if (cartItem == null || cartItem.isEmpty()) {
                throw new IllegalArgumentException("Giỏ hàng trống");
            }

            Map<Long, Integer> cartMap = cartItem.stream()
                    .collect(Collectors.toMap(CartItemResponse::getProductId, CartItemResponse::getQuantity));


            for (OrderItemRequest item : request.getItems()) {
                if (!cartMap.containsKey(item.getProductId())) {
                    throw new IllegalArgumentException("Sản phẩm " + item.getProductId() + " không có trong giỏ hàng");
                }
                if (item.getQuantity() > cartMap.get(item.getProductId())) {
                    throw new IllegalArgumentException("Số lượng sản phẩm " + item.getProductId() + " vượt quá trong giỏ hàng");
                }
            }
        }

        //lấy list productId truyền vào để check
        List<Long> productIds = request.getItems().stream()
                .map(OrderItemRequest::getProductId)
                .toList();

        Map<Long, Boolean> existsMap = productClient.checkProduct(productIds);

        for (OrderItemRequest item : request.getItems()) {
            if (!existsMap.getOrDefault(item.getProductId(), false)) {
                throw new IllegalArgumentException("Sản phẩm " + item.getProductId() + " không tồn tại");
            }
        }

        List<OrderDetail> details = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest item : request.getItems()) {
            ProductPriceResponse productPrice = productClient.productPrice(item.getProductId());

            OrderDetail detail = OrderDetail.builder()
                    .order(order)
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .unitPrice(productPrice.getOriginalPrice())
                    .discount(productPrice.getDiscountPercent())
                    .subTotal(productPrice.getFinalPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .createdAt(LocalDateTime.now())
                    .build();
            orderDetailRepository.save(detail);
            details.add(detail);
            totalAmount = totalAmount.add(detail.getSubTotal());
        }

        order.setTotalAmount(totalAmount);
        order.setOrderDetails(details);
        OrderCreateResponse response = orderMapper.toDto(order);

        PaymentKafkaEvent payment = PaymentKafkaEvent.builder()
                .orderId(order.getId())
                .userId(userId)
                .orderCode(order.getOrderCode())
                .totalAmount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .timestamp(LocalDateTime.now())
                .build();
        paymentKafka.send("payment-topic", payment);

        for (OrderDetail d : details) {
            String skuCode = productClient.getSkuCode(d.getProductId());
            InventoryKafkaEvent event = InventoryKafkaEvent.builder()
                    .quantity(d.getQuantity())
                    .skuCode(skuCode)
                    .build();
            inventoryKafka.send("place-order", event);
        }

        if (request.isFromCart()) {
            cartClient.clearSelectedCartItems(productIds);
        }

        return response;
    }

    @Override
    public ApiResponse<Page<OrdersAD>> getOrders(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

            Page<OrdersAD> response = orderRepository.getAll(pageable);

            List<Long> ids = response.getContent()
                    .stream()
                    .map(OrdersAD::getCustomerId)
                    .distinct()
                    .toList();

            List<Long> orders = response.getContent()
                    .stream()
                    .map(OrdersAD::getId)
                    .toList();

            CompletableFuture<Map<Long, String>> usersFuture = CompletableFuture.supplyAsync(() ->
                    userClient.extractFullName(ids), contextAwareExecutor);
            CompletableFuture<Map<Long, PaymentStatus>> paymentsFuture = CompletableFuture.supplyAsync(() ->
                    paymentClient.extractPaymentStatus(orders), contextAwareExecutor);

            CompletableFuture.allOf(usersFuture, paymentsFuture).join();

            Map<Long, String> usersMap = usersFuture.get();
            Map<Long, PaymentStatus> paymentStatusMap = paymentsFuture.get();

            response.getContent().forEach(item -> {
                item.setCustomerName(usersMap.get(item.getCustomerId()));
                item.setPaymentStatus(paymentStatusMap.get(item.getId()));
                item.setFormattedDate();
            });

            return ApiResponse.<Page<OrdersAD>>builder()
                    .code(200)
                    .message("Lấy thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Page<OrdersAD>>builder()
                    .code(500)
                    .message("Lỗi hệ thống" + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public Map<Long, Long> extractOrderQuantity(List<Long> usersId) {
        List<OrderQuantityResponse> orders = orderRepository.countOrdersByUserIds(usersId);

        return orders.stream()
                .collect(Collectors.toMap(OrderQuantityResponse::getUserId, OrderQuantityResponse::getQuantity));
    }

    @Override
    public List<UserOrderDetailResponse> getUserOrderDetail(Long id) {
        List<UserOrderDetailResponse> response = orderRepository.findOrdersDetailByUserId(id);

        response.forEach(UserOrderDetailResponse::setFormattedDate);
        return response;
    }

    @Override
    @Transactional
    public UpdateOrderStatusResponse updateOrderStatus(UpdateOrderStatusRequest request) {
        Orders orders = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoSuchElementException("Không có đơn hàng với id = " + request.getOrderId()));

        if (!orderStatusUtil.isValidTransition(orders.getStatus(), request.getOrderStatus())) {
            throw new IllegalArgumentException("Không thể chuyển từ "
                    + orders.getStatus() + " sang " + request.getOrderStatus());
        }

        orders.setStatus(request.getOrderStatus());
        orderRepository.save(orders);

        return UpdateOrderStatusResponse.builder()
                .orderId(orders.getId())
                .orderStatus(orders.getStatus().toString())
                .build();
    }

    @Override
    public boolean existsByUserIdAndProductIdAndStatus(Long userId, Long productId) {
        return orderRepository.existsByUserIdAndProductIdAndStatus(userId, productId, OrderStatus.DELIVERED);
    }
}
