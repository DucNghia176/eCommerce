package ecommerce.orderservice.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.apicommon1.model.request.UpdateOrderStatusRequest;
import ecommerce.orderservice.dto.response.ShippingResponse;
import ecommerce.orderservice.entity.Orders;
import ecommerce.orderservice.repository.OrderRepository;
import ecommerce.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;

@RequiredArgsConstructor
public class ShippingListener {
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "shipping-status-response", groupId = "order-group")
    public void consumeShippingStatus(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ShippingResponse response = mapper.readValue(message, ShippingResponse.class);
            Long orderId = response.getOrderId();

            // 🔹 Nếu không có orderId thì fallback sang orderCode
            if (orderId == null) {
                String orderCode = response.getOrderCode();
                if (orderCode == null) {
                    System.err.println("⚠ Kafka message thiếu cả orderId lẫn orderCode, bỏ qua: " + response);
                    return;
                }

                Orders orders = orderRepository.findOrdersByOrderCode(orderCode);
                if (orders == null) {
                    System.err.println("⚠ Không tìm thấy Orders với orderCode = " + orderCode);
                    return;
                }

                orderId = orders.getId();
            }
            System.out.println("📩 Nhận phản hồi shipping: " + response);

            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                    .orderId(orderId)
                    .orderStatus(response.getStatus())
                    .build();
            // Cập nhật đơn hàng
            orderService.updateOrderStatus(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
