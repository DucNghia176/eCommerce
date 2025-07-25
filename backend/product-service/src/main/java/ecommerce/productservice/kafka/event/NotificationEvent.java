package ecommerce.productservice.kafka.event;

import ecommerce.productservice.status.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationEvent {
    private String userId;
    private String content;
    private NotificationType type;
    private Long entityId;
}