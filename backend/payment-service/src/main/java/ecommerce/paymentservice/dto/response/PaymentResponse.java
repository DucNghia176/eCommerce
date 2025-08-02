package ecommerce.paymentservice.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private Long userId;
    private Long orderId;
    private String orderCode;
    private String paymentMethod;
    private String status;
}
