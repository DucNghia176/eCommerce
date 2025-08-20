package ecommerce.productservice.dto.response;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TagResponse {
    private Long id;
    private String name;
}
