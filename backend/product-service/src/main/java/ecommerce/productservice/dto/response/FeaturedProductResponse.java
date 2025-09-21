package ecommerce.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeaturedProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
}
