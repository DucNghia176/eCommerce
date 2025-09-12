package ecommerce.aipcommon.model.response;

import ecommerce.aipcommon.model.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderDetailResponse {
    private Long orderId;
    private String orderCode;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private BigDecimal orderPrice;
    private String formattedDate;
    
    public UserOrderDetailResponse(Long orderId, String orderCode, LocalDateTime orderDate, OrderStatus orderStatus, BigDecimal orderPrice) {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.orderPrice = orderPrice;
    }

    public void setFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.ENGLISH);
        this.formattedDate = this.orderDate.format(formatter);
    }
}
