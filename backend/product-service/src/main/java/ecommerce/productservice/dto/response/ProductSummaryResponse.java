package ecommerce.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProductSummaryResponse {
    private Long id;
    private String name;
    private String image;
}
