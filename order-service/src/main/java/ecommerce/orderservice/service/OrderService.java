package ecommerce.orderservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.ProductResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.aipcommon.util.JwtUtil;
import ecommerce.orderservice.client.ProductClient;
import ecommerce.orderservice.client.UserClient;
import ecommerce.orderservice.dto.request.OrderRequest;
import ecommerce.orderservice.dto.response.OrderResponse;
import ecommerce.orderservice.entity.OrderDetail;
import ecommerce.orderservice.entity.Orders;
import ecommerce.orderservice.kafka.KafkaOrder;
import ecommerce.orderservice.mapper.OrderDetailMapper;
import ecommerce.orderservice.mapper.OrderMapper;
import ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;

    private final KafkaOrder kafkaOrder;
    private final JwtUtil jwtUtil;

    public ApiResponse<OrderResponse> createOrder(String authHeader, OrderRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();
            Long userId = jwtUtil.extractId(token);
            UserResponse user = null;
            try {
                user = userClient.getUsersById(userId);
            } catch (Exception e) {
                return ApiResponse.<OrderResponse>builder()
                        .code(404)
                        .message("Người dùng không tồn tại")
                        .data(null)
                        .build();
            }
            Orders orders = orderMapper.toOrderEntity(request);

            List<OrderDetail> orderDetails = orderDetailMapper.toEntityList(request.getOrderDetails());
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (OrderDetail detail : orderDetails) {
                ProductResponse product;
                try {
                    product = productClient.getProductById(detail.getProductId());
                } catch (Exception e) {
                    return ApiResponse.<OrderResponse>builder()
                            .code(404)
                            .message("Sản phẩm với ID " + detail.getProductId() + " không tồn tại")
                            .data(null)
                            .build();
                }
                detail.setOrder(orders);
                BigDecimal price = detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
                BigDecimal discount = detail.getDiscount() != null ? detail.getDiscount() : BigDecimal.ZERO;
                totalAmount = totalAmount.add(price.subtract(discount));
            }
            orders.setTotalAmount(totalAmount);
            orders.setOrderDetails(orderDetails);
            orders.setIsActive(1);
            orders.setUserId(userId);

            Orders saved = orderRepository.save(orders);

            OrderResponse response = orderMapper.toResponse(saved);
            // Gửi thông báo sang Kafka
            kafkaOrder.sendMessage("order-events", "Tạo đơn hàng thành công: ID " + saved.getId());
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
