package ecommerce.inventoryservice.dto.request;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {
    String skuCode;
    int quantity;
}
