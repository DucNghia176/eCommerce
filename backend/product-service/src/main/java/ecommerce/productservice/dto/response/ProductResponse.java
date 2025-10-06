package ecommerce.productservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long id;
    String name;
    String description;
    BigDecimal price;
    BigDecimal discountPrice;
    Long categoryId;
    String categoryName;
    BrandResponse brand;
    String unit;
    int quantity;
    Integer isActive;
    List<TagResponse> tags;
    List<String> imageUrls;
    String thumbnailUrl;
    String skuCode;
    List<ProductAttributeResponse> attributes;

    public ProductResponse(Long id, String name, BigDecimal price, String categoryName, String description, BrandResponse brand, String thumbnailUrl, String skuCode) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryName = categoryName;
        this.description = description;
        this.brand = brand;
        this.thumbnailUrl = thumbnailUrl;
        this.skuCode = skuCode;
    }
}

