package ecommerce.orderservice.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailRequest {
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice; // Giá mỗi sản phẩm
    private BigDecimal discount;
}
