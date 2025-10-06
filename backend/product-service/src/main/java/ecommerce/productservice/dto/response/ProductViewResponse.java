package ecommerce.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewResponse {
    private Long id;
    private String name;
    private String skuCode;
    private Integer quantity;
    private BigDecimal price;
    private String brand;
    private String category;
    private Double score;
    private Long user;
    private List<String> imageUrls;
    private List<ProductAttributeResponse> attributes;
    private List<Long> relatedProducts;

    public ProductViewResponse(Long id, String name, String skuCode, BigDecimal price, String brand, String category, Double score, Long user) {
        this.id = id;
        this.name = name;
        this.skuCode = skuCode;
        this.price = price;
        this.brand = brand;
        this.category = category;
        this.score = score;
        this.user = user;
    }
}
