package ecommerce.productservice.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private Long parentId;
}
