package ecommerce.notificationservice.kafka.consumer;

import ecommerce.notificationservice.entity.Notification;
import ecommerce.notificationservice.kafka.event.NotificationEvent;
import ecommerce.notificationservice.repository.NotificationRepository;
import ecommerce.notificationservice.status.NotificationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @KafkaListener(topics = "new-product-topic", groupId = "notification-group")
    public void handleNotification(NotificationEvent event) {
        log.info("📩 Nhận thông báo từ Kafka: {}", event);

        Notification notification = Notification.builder()
                .userId(event.getUserId())
                .content(event.getContent())
                .type(event.getType())
                .entityId(event.getEntityId())
                .status(NotificationStatus.UNREAD)
                .createAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);

        // Gửi qua WebSocket nếu có
        if (event.getUserId() == null) {
            // Broadcast cho tất cả user
            simpMessagingTemplate.convertAndSend("/topic/notifications", saved);
        } else {
            // Gửi riêng cho một user cụ thể
            simpMessagingTemplate.convertAndSendToUser(String.valueOf(event.getUserId()), "/queue/notifications", saved);
        }
    }
}
