package ecommerce.inventoryservice.kafka.listener;

import ecommerce.aipcommon.kafka.event.InventoryKafkaEvent;
import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.inventoryservice.dto.request.InventoryRequest;
import ecommerce.inventoryservice.dto.response.InventoryResponse;
import ecommerce.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryListenerToOrder {
    private final InventoryService inventoryService;

    @KafkaListener(topics = "place-order", groupId = "inventory-group")
    public void handlePlaceOrder(List<InventoryKafkaEvent> items) {
        for (InventoryKafkaEvent item : items) {
            log.info("Nhận được đơn hàng từ OrderService: SKU={}, quantity={}", item.getSkuCode(), item.getQuantity());

            ApiResponse<InventoryResponse> response = inventoryService.confirmOrder(
                    new InventoryRequest(item.getSkuCode(), item.getQuantity())
            );

            if (response.getCode() != 200) {
                log.error("Lỗi khi cập nhật kho cho SKU {}: {}", item.getSkuCode(), response.getMessage());
            } else {
                log.info("Cập nhật kho thành công cho SKU {}: Còn {} sản phẩm", item.getSkuCode(), response.getData().getQuantity());
            }
        }
    }
}
