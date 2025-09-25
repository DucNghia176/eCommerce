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
    private String productName;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal discount;
    private Double rating;
    private Long totalUser;
}
