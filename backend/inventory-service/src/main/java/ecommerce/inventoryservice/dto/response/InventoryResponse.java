package ecommerce.inventoryservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    String skuCode;
    int quantity;
    int reservedQuantity;
    BigDecimal importPrice;
}
