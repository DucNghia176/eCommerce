package ecommerce.productservice.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SearchRequest {
    private String keyword;
    private Long categoryId;
    private Long brandId;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private Double ratingFrom;
}
