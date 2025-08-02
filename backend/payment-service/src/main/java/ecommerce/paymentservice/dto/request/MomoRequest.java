package ecommerce.paymentservice.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class MomoRequest {
    private Long orderId;
    //    private Long userId;
    private BigDecimal amount;
}
