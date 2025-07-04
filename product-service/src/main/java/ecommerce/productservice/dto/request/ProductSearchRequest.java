package ecommerce.productservice.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@Data
public class ProductSearchRequest {
    private String name;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String categoryName;
    private List<Long> tagName;
    private Boolean hasDiscount;
}
