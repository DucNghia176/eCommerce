package ecommerce.aipcommon.model.response;

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
    String categoryName;
    String brandId;
    String unit;
    Integer isActive;
    List<String> tags;
    List<String> imageUrls;
    String thumbnailUrl;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

