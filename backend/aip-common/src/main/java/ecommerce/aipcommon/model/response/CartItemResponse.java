package ecommerce.aipcommon.model.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String skuCode;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private Integer isSelected;
}