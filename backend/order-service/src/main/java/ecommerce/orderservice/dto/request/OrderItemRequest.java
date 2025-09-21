package ecommerce.orderservice.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequest {
    private Long productId;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
}
