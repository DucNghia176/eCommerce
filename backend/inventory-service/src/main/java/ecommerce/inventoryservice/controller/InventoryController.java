package ecommerce.inventoryservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.inventoryservice.dto.request.InventoryRequest;
import ecommerce.inventoryservice.dto.response.InventoryResponse;
import ecommerce.inventoryservice.entity.Inventory;
import ecommerce.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/inventory")
class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/check")
    public boolean isInStock(@RequestParam InventoryRequest request) {
        return inventoryService.isInStock(request);
    }

    @GetMapping("/findSkuCode")
    public Optional<Inventory> findBySkuCode(@RequestParam String skuCode) {
        return inventoryService.findBySkuCode(skuCode);
    }

    @PostMapping("/update/quantity")
    public ApiResponse<InventoryResponse> importQuantity(@Valid @RequestBody InventoryRequest request) {
        return inventoryService.importQuantity(request);
    }

    @PutMapping("cart")
    public ApiResponse<InventoryResponse> updateCart(@Valid @RequestBody InventoryRequest request) {
        return inventoryService.updateInventoryFortCart(request);
    }

    @PutMapping("order/confirm")
    public ApiResponse<InventoryResponse> importOrder(@Valid @RequestBody InventoryRequest request) {
        return inventoryService.confirmOrder(request);
    }

    @PutMapping("order/cancel")
    public ApiResponse<InventoryResponse> cancelOrder(@Valid @RequestBody InventoryRequest request) {
        return inventoryService.cancelOrder(request);
    }
}
