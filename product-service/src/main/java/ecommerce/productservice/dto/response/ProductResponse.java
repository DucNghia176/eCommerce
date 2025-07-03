package ecommerce.productservice.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String categoryName;
    private String brandId;
    private String unit;
    private Integer isActive;
    private List<String> tags;
    private List<String> imageUrls;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
}

