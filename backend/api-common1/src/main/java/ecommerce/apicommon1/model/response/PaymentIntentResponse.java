package ecommerce.apicommon1.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentIntentResponse {
    private Long orderId;
    private BigDecimal totalAmount;
    private String checkoutUrl;
}
