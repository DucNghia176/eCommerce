package ecommerce.productservice.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Long categoryId;
    private String brandId;
    private String unit;
    private List<String> imageUrls;
    private String thumbnailUrl;
    private Integer isActive;
    private List<Long> tags;
}
