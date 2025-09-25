package ecommerce.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildCategoryResponse {
    private Long id;
    private String name;
    private List<BrandResponse> brands;
    private List<FeaturedProductResponse> products;
}
