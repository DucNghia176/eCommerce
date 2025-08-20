package ecommerce.productservice.dto.response;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@Getter
@Setter
public class BrandResponse {
    private Long id;
    private String name;
}
