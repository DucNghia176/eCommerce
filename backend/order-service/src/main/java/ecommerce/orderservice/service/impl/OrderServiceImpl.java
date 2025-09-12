package ecommerce.orderservice.service.impl;

import ecommerce.aipcommon.config.TokenInfo;
import ecommerce.aipcommon.kafka.event.InventoryKafkaEvent;
import ecommerce.aipcommon.kafka.event.PaymentKafkaEvent;
import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.CartItemResponse;
import ecommerce.aipcommon.model.response.UserOrderDetailResponse;
import ecommerce.aipcommon.model.status.PaymentStatus;
import ecommerce.orderservice.client.CartClient;
import ecommerce.orderservice.client.PaymentClient;
import ecommerce.orderservice.client.ProductClient;
import ecommerce.orderservice.client.UserClient;
import ecommerce.orderservice.dto.request.OrderRequest;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
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
            order.setOrderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDateTime.now());
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

        Map<Long, Long> response = orders.stream()
                .collect(Collectors.toMap(OrderQuantityResponse::getUserId, OrderQuantityResponse::getQuantity));
        return response;
    }

    @Override
    public List<UserOrderDetailResponse> getUserOrderDetail(Long id) {
        List<UserOrderDetailResponse> response = orderRepository.findOrdersDetailByUserId(id);

        response.forEach(UserOrderDetailResponse::setFormattedDate);
        return response;
    }
}
