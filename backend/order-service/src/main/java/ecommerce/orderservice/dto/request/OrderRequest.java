package ecommerce.orderservice.dto.request;

import ecommerce.apicommon1.model.status.PaymentMethodStatus;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private String shippingAddress;
    private Map<Long, Boolean> selectedCartItemIds;
    private PaymentMethodStatus paymentMethod;
    private String note;
}
