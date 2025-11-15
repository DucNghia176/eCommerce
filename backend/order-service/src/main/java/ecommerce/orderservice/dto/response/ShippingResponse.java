package ecommerce.orderservice.dto.response;

import ecommerce.apicommon1.model.status.OrderStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingResponse {
    private Long orderId;
    private String orderCode;
    private OrderStatus status;
    private boolean delivered;
    private long timestamp;
    private Object fabric;
}

