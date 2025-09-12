package ecommerce.orderservice.dto.response;

import ecommerce.aipcommon.model.status.OrderStatus;
import ecommerce.aipcommon.model.status.PaymentMethodStatus;
import ecommerce.aipcommon.model.status.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersAD {
    private Long id;
    private String orderCode;
    private LocalDateTime orderDate;
    private Long customerId;
    private String customerName;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private BigDecimal orderAmount;
    private String formattedDate;
    private PaymentMethodStatus paymentMethod;

    public OrdersAD(Long id, String orderCode, LocalDateTime orderDate, Long customerId, OrderStatus orderStatus, BigDecimal orderAmount, PaymentMethodStatus paymentMethod) {
        this.id = id;
        this.orderCode = orderCode;
        this.orderDate = orderDate;
        this.customerId = customerId;
        this.orderStatus = orderStatus;
        this.orderAmount = orderAmount;
        this.paymentMethod = paymentMethod;
    }

    public void setFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.ENGLISH);
        this.formattedDate = this.orderDate.format(formatter);
    }
}
