package ecommerce.paymentservice.dto.request;

import ecommerce.aipcommon.model.status.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class PaymentRequest {
    private Long orderId;
    private PaymentStatus paymentStatus;
}
