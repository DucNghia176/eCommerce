package ecommerce.apicommon1.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductPriceResponse {
    private Long productId;
    private BigDecimal originalPrice;
    private BigDecimal discountPercent;
    private BigDecimal finalPrice;
}
