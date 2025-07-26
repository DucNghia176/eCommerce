package ecommerce.inventoryservice.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {
    String skuCode;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    int quantity;
}
