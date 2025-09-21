package ecommerce.apicommon1.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Data
public class CartItemResponse {
    //    String skuCode;
    private Long id;
    private Long productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;

    public CartItemResponse(Long id, Long productId, int quantity, BigDecimal unitPrice, BigDecimal discount) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discount = discount;
    }
}