package ecommerce.orderservice.dto.request;

import ecommerce.apicommon1.model.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderStatusRequest {
    Long orderId;
    OrderStatus orderStatus;
}
