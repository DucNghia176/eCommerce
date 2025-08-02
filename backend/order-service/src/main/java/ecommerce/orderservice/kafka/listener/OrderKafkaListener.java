package ecommerce.orderservice.kafka.listener;

import ecommerce.aipcommon.kafka.event.InventoryKafkaEvent;
import ecommerce.aipcommon.kafka.event.PaymentKafkaEvent;
import ecommerce.aipcommon.model.status.OrderStatus;
import ecommerce.orderservice.client.ProductClient;
import ecommerce.orderservice.entity.OrderDetail;
import ecommerce.orderservice.entity.Orders;
import ecommerce.orderservice.repository.OrderDetailRepository;
import ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaListener {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductClient productClient;
    private final KafkaTemplate<String, InventoryKafkaEvent> inventoryKafka;

    @KafkaListener(topics = "payment-confirm", groupId = "order-group")
    public void ListenPaymentConfirm(PaymentKafkaEvent event) {
        try {
            Orders order = orderRepository.findById(event.getOrderId()).orElse(null);
            if (order == null) {
                log.error("Không tìm thấy đơn hàng với ID: {}", event.getOrderId());
                return;
            }
            log.info("Nhận thông tin thanh toán: {}", event);
            if (event.getStatus().toString().equals("SUCCESS")) {
                order.setStatus(OrderStatus.CONFIRMED);
                order.setUpdatedAt(event.getTimestamp());
                orderRepository.save(order);

                List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());

                for (OrderDetail detail : details) {
                    String skuCode = productClient.getSkuCode(detail.getProductId());

                    InventoryKafkaEvent inventoryEvent = InventoryKafkaEvent.builder()
                            .skuCode(skuCode)
                            .reservedQuantity(detail.getQuantity())
                            .build();

                    inventoryKafka.send("confirm-order", inventoryEvent);
                }
            } else if (event.getStatus().toString().equals("FAILED")) {
                order.setStatus(OrderStatus.FAILED);
                order.setUpdatedAt(event.getTimestamp());
                orderRepository.save(order);
            }
        } catch (Exception e) {
            log.error("Lỗi xử lý Kafka đơn hàng thanh toán: {}", e.getMessage(), e);
        }
    }
}
