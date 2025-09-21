package ecommerce.apicommon1.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateOrderStatusResponse {
    Long orderId;
    Long userId;
    String orderStatus;
}
