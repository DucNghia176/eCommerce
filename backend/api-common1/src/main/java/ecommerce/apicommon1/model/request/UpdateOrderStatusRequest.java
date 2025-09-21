package ecommerce.apicommon1.model.request;

import ecommerce.apicommon1.model.status.OrderStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOrderStatusRequest {
    Long orderId;
    OrderStatus orderStatus;
}
