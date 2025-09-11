package ecommerce.aipcommon.model.response;

import ecommerce.aipcommon.model.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderDetailResponse {
    Long orderId;
    String orderCode;
    LocalDateTime orderDate;
    OrderStatus orderStatus;
    BigDecimal orderPrice;
}
