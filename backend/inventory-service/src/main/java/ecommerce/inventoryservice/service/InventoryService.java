package ecommerce.inventoryservice.service;

import ecommerce.inventoryservice.entity.Inventory;

import java.util.Optional;

public interface InventoryService {
    boolean isInStock(String skuCode, int quantity);

    Optional<Inventory> findBySkuCode(String skuCode);

//    ApiResponse<Void> updateInventory(Inventory inventory);
}
