package ecommerce.orderservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.CartResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.orderservice.client.CartClient;
import ecommerce.orderservice.dto.request.OrderRequest;
import ecommerce.orderservice.dto.response.OrderResponse;
import ecommerce.orderservice.entity.OrderDetail;
import ecommerce.orderservice.entity.Orders;
import ecommerce.orderservice.kafka.KafkaOrder;
import ecommerce.orderservice.mapper.OrderMapper;
import ecommerce.orderservice.repository.OrderRepository;
import ecommerce.orderservice.util.OrderValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final KafkaOrder kafkaOrder;

    private final CartClient cartClient;
    private final OrderValidator validator;

    public ApiResponse<OrderResponse> createOrder(String authHeader, OrderRequest request) {
        try {
            Long userId = validator.extractUserId(authHeader);
            UserResponse user = validator.validateUser(userId);
            List<CartResponse> selectedItems;
            try {
                selectedItems = cartClient.getSelectedCartItems(userId);
            } catch (Exception e) {
                return ApiResponse.<OrderResponse>builder()
                        .code(500)
                        .message("Không thể lấy danh sách giỏ hàng")
                        .data(null)
                        .build();
            }
            if (selectedItems.isEmpty()) {
                return ApiResponse.<OrderResponse>builder()
                        .code(400)
                        .message("Giỏ hàng trống hoặc chưa chọn sản phẩm nào")
                        .data(null)
                        .build();
            }
            Orders order = orderMapper.toOrderEntity(request);
            order.setUserId(userId);
            order.setIsActive(1);

            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderDetail> orderDetails = new ArrayList<>();

            for (CartResponse item : selectedItems) {
                BigDecimal discount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;
                BigDecimal price = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                BigDecimal itemTotal = price.subtract(discount);

                OrderDetail orderDetail = OrderDetail.builder()
                        .order(order)
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .discount(item.getDiscount())
                        .build();
                orderDetails.add(orderDetail);
                totalAmount = totalAmount.add(itemTotal);
            }
            order.setTotalAmount(totalAmount);
            order.setOrderDetails(orderDetails);
            order.setIsActive(1);

            Orders saved = orderRepository.save(order);

            cartClient.clearSelectedCartItems(authHeader);

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
