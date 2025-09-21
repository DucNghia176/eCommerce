package ecommerce.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChildCategoryResponse {
    private Long id;
    private String name;
    private List<FeaturedProductResponse> products;
}
