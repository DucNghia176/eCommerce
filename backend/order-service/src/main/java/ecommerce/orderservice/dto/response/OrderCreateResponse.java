package ecommerce.orderservice.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateResponse {
    private Long id;
    private Long userId;
    private String shippingAddress;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderDetailResponse> orderDetails;
    private String checkoutUrl;
}