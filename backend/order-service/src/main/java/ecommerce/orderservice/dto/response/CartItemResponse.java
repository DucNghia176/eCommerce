package ecommerce.orderservice.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItemResponse {
    private Long id;
    private int quantity;
    private Long productId;
    private BigDecimal unitPrice;
    private BigDecimal discount;
}