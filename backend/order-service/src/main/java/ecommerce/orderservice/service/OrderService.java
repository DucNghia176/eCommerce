package ecommerce.orderservice.service;

import ecommerce.aipcommon.config.TokenInfo;
import ecommerce.aipcommon.kafka.event.InventoryKafkaEvent;
import ecommerce.aipcommon.kafka.event.PaymentKafkaEvent;
import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.status.OrderStatus;
import ecommerce.orderservice.client.CartClient;
import ecommerce.orderservice.client.ProductClient;
import ecommerce.orderservice.dto.request.OrderRequest;
import ecommerce.orderservice.dto.response.CartItemResponse;
import ecommerce.orderservice.dto.response.OrderResponse;
import ecommerce.orderservice.entity.OrderDetail;
import ecommerce.orderservice.entity.Orders;
import ecommerce.orderservice.kafka.KafkaOrder;
import ecommerce.orderservice.mapper.OrderMapper;
import ecommerce.orderservice.repository.OrderDetailRepository;
import ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final KafkaOrder kafkaOrder;

    private final CartClient cartClient;

    private final TokenInfo tokenInfo;
    private final OrderDetailRepository orderDetailRepository;
    private final KafkaTemplate<String, InventoryKafkaEvent> inventoryKafka;
    private final KafkaTemplate<String, PaymentKafkaEvent> paymentKafka;
    private final ProductClient productClient;

    @Transactional
    public ApiResponse<OrderResponse> placeOrder(OrderRequest request) {
        try {
            Long userId = tokenInfo.getUserId();

            List<CartItemResponse> cart = cartClient.getSelectedCartItem(request.getSelectedCartItemIds());

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
            order.setUserId(userId); // gán userId từ cart
            order.setTotalAmount(totalAmount);
            order.setCreatedAt(LocalDateTime.now());
            order.setStatus(OrderStatus.PENDING);
            order.setUpdatedAt(LocalDateTime.now());

            // 4. Lưu tạm order để có ID
            orderRepository.save(order);

            // 5. Tạo danh sách OrderDetail từ CartItemResponse
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
                    .totalAmount(order.getTotalAmount())
                    .build();
            paymentKafka.send("payment-topic", payment);

            List<InventoryKafkaEvent> inventory = cart.stream().map(item -> {
                // Gọi product-service để lấy skuCode từ productId
                String skuCode = productClient.getSkuCode(item.getProductId());

                return new InventoryKafkaEvent(
                        skuCode,
                        item.getQuantity()
                );
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
            log.error("Lỗi: " + e.getMessage(), e);
            // Gửi thông báo sang Kafka
            kafkaOrder.sendMessage("order-events", "Tạo đơn hàng thất bại");
            return ApiResponse.<OrderResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();

        }
    }
}
