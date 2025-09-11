package ecommerce.inventoryservice.repository;

import ecommerce.inventoryservice.dto.response.GetSkuCodeQuantity;
import ecommerce.inventoryservice.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
    Optional<Inventory> findBySkuCode(String skuCode);

    List<GetSkuCodeQuantity> findBySkuCodeIn(List<String> skuCodes);
}
