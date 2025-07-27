package ecommerce.aipcommon.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateEvent {
    Long orderId;
    Long userId;
    LocalDateTime createdAt;
    String status;
}
