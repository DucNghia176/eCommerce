package ecommerce.productservice.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CategoryRequest {
    private String name;
    private Long parentId;
    private Integer isVisible;
}