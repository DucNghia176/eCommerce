package ecommerce.productservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductViewResponse extends ProductResponse {
    private Double score;
    private Long user;
    private List<Long> relatedProducts;

    public ProductViewResponse(Long id, String name, String skuCode, BigDecimal price, String brand, String category, Double score, Long user) {
        super.setId(id);
        super.setName(name);
        super.setSkuCode(skuCode);
        super.setPrice(price);
        super.setBrand(BrandResponse.builder()
                .name(brand)
                .build());
        super.setCategoryName(category);
        this.score = score;
        this.user = user;
    }
}
