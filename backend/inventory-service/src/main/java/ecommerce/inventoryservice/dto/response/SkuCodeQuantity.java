package ecommerce.inventoryservice.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuCodeQuantity {
    String skuCode;
    int quantity;
}
