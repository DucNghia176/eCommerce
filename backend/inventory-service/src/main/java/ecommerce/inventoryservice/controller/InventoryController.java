package ecommerce.inventoryservice.controller;

import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.inventoryservice.dto.request.InventoryRequest;
import ecommerce.inventoryservice.dto.response.InventoryResponse;
import ecommerce.inventoryservice.entity.Inventory;
import ecommerce.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
@RequestMapping("/api/inventory")
class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/quantity")
    public int getQuantities(@RequestParam("skuCode") String skuCode) {
        return inventoryService.getQuantity(skuCode);
    }

    @GetMapping("/quantities")
    public Map<String, Integer> getQuantities(@RequestParam("skuCodes") List<String> skuCodes) {
        return inventoryService.extractSkuCodes(skuCodes);
    }

    @GetMapping("/check")
    public boolean isInStock(@RequestParam String skuCode, @RequestParam int quantity) {
        return inventoryService.isInStock(skuCode, quantity);
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

    @PutMapping("orders/confirm")
    public ApiResponse<InventoryResponse> importOrder(@Valid @RequestBody InventoryRequest request) {
        return inventoryService.confirmOrder(request);
    }

    @PutMapping("orders/cancel")
    public ApiResponse<InventoryResponse> cancelOrder(@Valid @RequestBody InventoryRequest request) {
        return inventoryService.cancelOrder(request);
    }
}
