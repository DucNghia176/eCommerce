package ecommerce.inventoryservice.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    LocalDateTime importedAt;
}
