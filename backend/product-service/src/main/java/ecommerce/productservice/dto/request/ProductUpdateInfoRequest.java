package ecommerce.productservice.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@Data
public class ProductUpdateInfoRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    private Long categoryId;
    private Long brandId;
    private List<Long> tags;
    private String unit;
    private List<AttributeRequest> attributes;
}
