package ecommerce.notificationservice.kafka.event;

import ecommerce.notificationservice.status.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationEvent {
    private Long userId;
    private String content;
    private NotificationType type;
    private Long entityId;
}