package ecommerce.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProductResponse {
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
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String skuCode;
    List<ProductAttributeResponse> attributes;
}
