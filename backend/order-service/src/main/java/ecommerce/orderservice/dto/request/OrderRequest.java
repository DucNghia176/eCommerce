package ecommerce.orderservice.dto.request;

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
    private String paymentMethod;
    private String note;
}
