package ecommerce.aipcommon.kafka.event;

import ecommerce.aipcommon.model.status.PaymentMethodStatus;
import ecommerce.aipcommon.model.status.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentKafkaEvent {
    Long orderId;
    String orderCode;
    Long userId;
    BigDecimal totalAmount;
    PaymentMethodStatus paymentMethod;
    LocalDateTime timestamp;
    PaymentStatus status;
}