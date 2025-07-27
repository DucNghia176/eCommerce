package ecommerce.inventoryservice.kafka.listener;

import ecommerce.aipcommon.kafka.event.ProductCreateEvent;
import ecommerce.inventoryservice.entity.Inventory;
import ecommerce.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryListenerToProduct {
    private final InventoryRepository inventoryRepository;

    @KafkaListener(topics = "product-create", groupId = "inventory-group")
    public void handleProductCreated(ProductCreateEvent event) {
        log.info("📦 Nhận event tạo sản phẩm: {}", event);
        Inventory inventory = Inventory.builder()
                .skuCode(event.getSkuCode())
                .name(event.getName())
                .quantity(0)
                .importPrice(event.getImportPrice())
                .importedAt(LocalDateTime.now())
                .build();
        inventoryRepository.save(inventory);

        log.info("Đã lưu kho cho SKU: {}", event.getSkuCode());
    }
}

