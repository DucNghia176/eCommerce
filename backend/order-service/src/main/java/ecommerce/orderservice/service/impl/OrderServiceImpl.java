package ecommerce.orderservice.service.impl;

import ecommerce.apicommon1.config.TokenInfo;
import ecommerce.apicommon1.kafka.event.InventoryKafkaEvent;
import ecommerce.apicommon1.kafka.event.PaymentKafkaEvent;
import ecommerce.apicommon1.kafka.event.ShippingKafkaEvent;
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
import ecommerce.orderservice.dto.response.*;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
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
    private final ProductClient productClient;
    private final UserClient userClient;
    private final PaymentClient paymentClient;
    private final Executor contextAwareExecutor;
    private final OrderStatusUtil orderStatusUtil;
    private final GenerateKey generateKey;

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
            return orderMapper.toDto(existingOrder.get());
        }

        //lưu tạm order
        Orders order = Orders.builder()
                .userId(userId)
                .shippingAddress(request.getShippingAddress())
                .status(PAYMENT_FLOW.getOrDefault(request.getPaymentMethod(), OrderStatus.PENDING))
                .paymentMethod(request.getPaymentMethod())
                .isActive(1)
                .orderCode(generateKey.generateOrderCode())
                .createdAt(LocalDateTime.now())
                .note(request.getNote())
                .cartSignature(cartSignature)
                .build();
        orderRepository.save(order);

        // nếu đặt hàng từ cart
        if (request.isFromCart()) {
            ApiResponse<CartResponse> cartRes = cartClient.getCartByUserId();
            List<CartItemResponse> cartItem = cartRes.getData().getItems();

            if (cartItem == null || cartItem.isEmpty()) {
                throw new IllegalArgumentException("Giỏ hàng trống");
            }

            // Map productId -> CartItemResponse để tiện so sánh
            Map<Long, CartItemResponse> cartMap = cartItem.stream()
                    .collect(Collectors.toMap(CartItemResponse::getProductId, item -> item));

            for (OrderItemRequest item : request.getItems()) {
                CartItemResponse cartProduct = cartMap.get(item.getProductId());
                if (cartProduct == null) {
                    throw new IllegalArgumentException("Sản phẩm " + item.getProductId() + " không có trong giỏ hàng");
                }
                if (item.getQuantity() > cartProduct.getQuantity()) {
                    throw new IllegalArgumentException("Số lượng sản phẩm " + item.getProductId() + " vượt quá trong giỏ hàng");
                }

                //Check giá hiện tại với giá trong giỏ
                ProductSimpleResponse productPrice = productClient.productPrice(item.getProductId());
                BigDecimal currentPrice = productPrice.getFinalPrice();
                BigDecimal cartPrice = cartProduct.getUnitPrice();

                if (cartPrice.compareTo(currentPrice) != 0) {
                    throw new IllegalArgumentException(
                            "Giá sản phẩm " + item.getProductId() + " đã thay đổi. " +
                                    "Giá cũ: " + cartPrice + ", giá mới: " + currentPrice
                    );
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
            ProductSimpleResponse productPrice = productClient.productPrice(item.getProductId());

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
        kafkaOrder.sendMessage("payment-topic", payment);

        List<ShippingKafkaEvent.ProductItem> productItems = details.stream()
                .map(d -> ShippingKafkaEvent.ProductItem.builder()
                        .productId(d.getProductId())
                        .productName(productClient.productPrice(d.getProductId()).getProductName())
                        .quantity(d.getQuantity())
                        .price(d.getUnitPrice())
                        .build())
                .toList();
        ShippingKafkaEvent shipping = ShippingKafkaEvent.builder()
                .orderId(order.getId())
                .userId(userId)
                .orderCode(order.getOrderCode())
                .shippingAddress(request.getShippingAddress())
                .totalAmount(totalAmount)
                .items(productItems)
                .createdAt(LocalDateTime.now())
                .build();
        kafkaOrder.sendMessage("shipping-topic", shipping);

        for (OrderDetail d : details) {
            String skuCode = productClient.getSkuCode(d.getProductId());
            InventoryKafkaEvent event = InventoryKafkaEvent.builder()
                    .quantity(d.getQuantity())
                    .skuCode(skuCode)
                    .build();
            kafkaOrder.sendMessage("place-order", event);
        }

        if (request.isFromCart()) {
            cartClient.clearSelectedCartItems(productIds);
        }

        if (request.getPaymentMethod() == PaymentMethodStatus.PAYPAL) {
            ApiResponse<PaymentIntentResponse> apiRes =
                    paymentClient.createPayment(order.getId(), order.getTotalAmount());
            PaymentIntentResponse paymentIntent = apiRes.getData();
            response.setCheckoutUrl(paymentIntent.getCheckoutUrl());
        }

        return response;
    }

    @Override
    public ApiResponse<Page<OrdersAD>> getOrders(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

            Page<OrdersAD> response = orderRepository.getAll(pageable);

            // Lấy list customerId
            List<Long> customerIds = response.getContent()
                    .stream()
                    .map(OrdersAD::getCustomerId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            // Lấy list orderIds
            List<Long> orderIds = response.getContent()
                    .stream()
                    .map(OrdersAD::getId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            orderIds.forEach(id -> System.out.println("ORDER-ID: " + id));

            // Gọi tuần tự (sync)
            Map<Long, String> usersMap = userClient.extractFullName(customerIds);
            Map<Long, PaymentStatus> paymentStatusMap = paymentClient.extractPaymentStatus(orderIds);

            // Gộp dữ liệu
            response.getContent().forEach(item -> {
                item.setCustomerName(usersMap.get(item.getCustomerId()));

                PaymentStatus status = paymentStatusMap.getOrDefault(
                        item.getId(),
                        PaymentStatus.NOT_PAID
                );
                item.setPaymentStatus(status);
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
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public PaymentOrderResponse getOrderById(Long orderId) {
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Không có đơn hàng với id = " + orderId));

        return PaymentOrderResponse.builder()
                .orderId(orders.getId())
                .orderCode(orders.getOrderCode())
                .userId(orders.getUserId())
                .totalAmount(orders.getTotalAmount())
                .build();
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

        if (request.getOrderStatus() == OrderStatus.SHIPPING) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", orders.getId());
            payload.put("status", request.getOrderStatus().toString());
            payload.put("orderCode", orders.getOrderCode());
            kafkaOrder.sendMessage("shipping-update", payload);
        }

        return UpdateOrderStatusResponse.builder()
                .orderId(orders.getId())
                .orderStatus(orders.getStatus().toString())
                .build();
    }

    @Override
    public boolean existsByUserIdAndProductIdAndStatus(Long userId, Long productId) {
        return orderRepository.existsByUserIdAndProductIdAndStatus(userId, productId, OrderStatus.DELIVERED);
    }

    public List<OrderResponse> getOrderByUserId() {
        Long userId = tokenInfo.getUserId();
        List<Orders> orders = orderRepository.findOrdersByUserId(userId);

        return orders.stream().map(order -> {
            List<OrderDetailResponse> detail = order.getOrderDetails().stream()
                    .map(d -> {
                        String productName;
                        String imageUrl = null;
                        try {
                            productName = productClient.productPrice(d.getProductId()).getProductName();
                            imageUrl = productClient.productPrice(d.getProductId()).getImageUrl();
                        } catch (feign.FeignException.NotFound e) {
                            productName = "Sản phẩm đã bị xóa";
                        }

                        return OrderDetailResponse.builder()
                                .productId(d.getProductId())
                                .productName(productName)
                                .imageUrl(imageUrl)
                                .quantity(d.getQuantity())
                                .unitPrice(d.getUnitPrice())
                                .discount(d.getDiscount())
                                .createdAt(d.getCreatedAt())
                                .build();
                    })
                    .toList();

            return OrderResponse.builder()
                    .id(order.getId())
                    .userId(order.getUserId())
                    .orderCode(order.getOrderCode())
                    .shippingAddress(order.getShippingAddress())
                    .status(order.getStatus())
                    .totalAmount(order.getTotalAmount())
                    .isActive(order.getIsActive())
                    .createdAt(order.getCreatedAt())
                    .updatedAt(order.getUpdatedAt())
                    .orderDetails(detail)
                    .build();
        }).toList();
    }
}
