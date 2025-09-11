package ecommerce.paymentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TotalAmountByUserId {
    Long userId;
    BigDecimal totalAmount;
}
