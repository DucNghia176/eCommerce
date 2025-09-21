package ecommerce.productservice.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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

    @DecimalMin(value = "0.0", inclusive = false, message = "Discount phải > 0")
    @DecimalMax(value = "100.0", inclusive = false, message = "Discount phải < 100")
    private BigDecimal discountPrice;
    private Long categoryId;
    private String brandId;
    private String unit;
    private String skuCode;
    private List<Long> tags;
}
