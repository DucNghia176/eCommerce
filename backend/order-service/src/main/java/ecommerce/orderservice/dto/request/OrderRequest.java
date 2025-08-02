package ecommerce.orderservice.dto.request;

import ecommerce.aipcommon.model.status.PaymentMethodStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private String shippingAddress;
    private Map<Long, Boolean> selectedCartItemIds;
    private LocalDate orderDate;
    private PaymentMethodStatus paymentMethod;
    private String note;
}
