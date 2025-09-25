package ecommerce.inventoryservice.repository;

import ecommerce.inventoryservice.dto.response.GetSkuCodeQuantity;
import ecommerce.inventoryservice.entity.Inventory;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
    Optional<Inventory> findBySkuCode(String skuCode);

    List<GetSkuCodeQuantity> findBySkuCodeIn(List<String> skuCodes);


    @Transactional
    @Modifying
    @Query(
            value = "UPDATE INVENTORY_DB.INVENTORY i " +
                    "SET i.QUANTITY = i.QUANTITY - ?2, " +
                    "    i.RESERVED_QUANTITY = i.RESERVED_QUANTITY + ?2 " +
                    "WHERE i.SKU_CODE = ?1 " +
                    "  AND (i.QUANTITY - i.RESERVED_QUANTITY) >= ?2",
            nativeQuery = true
    )
    int reserveStock(String skuCode, int orderQty);
}
