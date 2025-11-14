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
public class ProductSimpleResponse {
    private Long productId;
    private String productName;
    private String imageUrl;
    private BigDecimal originalPrice;
    private BigDecimal discountPercent;
    private BigDecimal finalPrice;
}
