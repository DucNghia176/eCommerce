package ecommerce.inventoryservice.service.impl;

import ecommerce.inventoryservice.entity.Inventory;
import ecommerce.inventoryservice.repository.InventoryRepository;
import ecommerce.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    @Override
    public boolean isInStock(String skuCode, int quantity) {
        Inventory inventory = inventoryRepository.findById(skuCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã SKU: " + skuCode));
        return inventory.getQuantity() >= quantity;
    }

    @Override
    public Optional<Inventory> findBySkuCode(String skuCode) {
        return inventoryRepository.findBySkuCode(skuCode);
    }

}
