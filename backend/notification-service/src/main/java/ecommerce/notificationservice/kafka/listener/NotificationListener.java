package ecommerce.notificationservice.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationListener {
    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void listenOrder(String message) {
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void listenUser(String message) {
    }

    @KafkaListener(topics = "cart-events", groupId = "notification-group")
    public void listenCart(String message) {
    }
}
