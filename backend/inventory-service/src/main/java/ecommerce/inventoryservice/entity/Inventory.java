package ecommerce.inventoryservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "INVENTORY", schema = "INVENTORY_DB")
public class Inventory {
    @Id
    @Column(name = "SKU_CODE")
    private String skuCode;

    @Column(name = "NAME")
    private String name;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "RESERVED_QUANTITY")
    private Integer reservedQuantity;

    @Column(name = "IMPORT_PRICE")
    private BigDecimal importPrice;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "IMPORTED_AT")
    private LocalDateTime importedAt;
}
