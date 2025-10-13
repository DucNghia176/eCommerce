package ecommerce.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchProductResponse {
    private Long id;
    private String name;
    private String thumbnailUrl;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Double score;
    private Long user;
}
