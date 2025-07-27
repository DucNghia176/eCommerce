package ecommerce.aipcommon.kafka.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentKafkaEvent {
    Long orderId;
    Long userId;
    BigDecimal totalAmount;
}