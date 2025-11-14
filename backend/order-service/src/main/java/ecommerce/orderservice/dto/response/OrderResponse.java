package ecommerce.orderservice.dto.response;

import ecommerce.apicommon1.model.status.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String orderCode;
    private String shippingAddress;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Integer isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderDetailResponse> orderDetails;
}