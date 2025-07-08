package ecommerce.notificationservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationListener {
    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void listenOrder(String message) {
        log.info("Received message: {}", message);
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void listenUser(String message) {
        log.info("Received message: {}", message);
    }
}
