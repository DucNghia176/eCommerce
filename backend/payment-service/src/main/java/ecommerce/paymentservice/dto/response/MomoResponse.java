package ecommerce.paymentservice.dto.response;

import ecommerce.aipcommon.model.status.PaymentStatus;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MomoResponse {
    private Long orderId;
    private String payUrl;
    private PaymentStatus status;
}