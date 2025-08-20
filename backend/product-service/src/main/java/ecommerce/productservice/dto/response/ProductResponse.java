package ecommerce.productservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

