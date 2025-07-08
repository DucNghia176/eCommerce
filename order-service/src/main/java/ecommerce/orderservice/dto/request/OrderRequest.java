package ecommerce.orderservice.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private Long userId;
    private String shippingAddress;
    private String status;
    private List<OrderDetailRequest> orderDetails;
}
