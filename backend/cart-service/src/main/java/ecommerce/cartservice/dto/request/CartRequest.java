package ecommerce.cartservice.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartRequest {
    Long productId;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    int quantity;
}
