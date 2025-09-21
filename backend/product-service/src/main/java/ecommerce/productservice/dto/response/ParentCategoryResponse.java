package ecommerce.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentCategoryResponse {
    private Long id;
    private String name;
    private List<ChildCategoryResponse> children;
}
