package ecommerce.orderservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrderResponse {
    private Long orderId;
    private Long userId;
    private String orderCode;
    private BigDecimal totalAmount;
    private String status;
}
