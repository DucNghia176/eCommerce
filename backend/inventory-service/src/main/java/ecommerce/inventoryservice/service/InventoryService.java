package ecommerce.inventoryservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.inventoryservice.dto.request.InventoryRequest;
import ecommerce.inventoryservice.dto.response.InventoryResponse;
import ecommerce.inventoryservice.entity.Inventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InventoryService {
    boolean isInStock(String skuCode, int quantity);

    Optional<Inventory> findBySkuCode(String skuCode);

    ApiResponse<InventoryResponse> importQuantity(InventoryRequest request);

    ApiResponse<InventoryResponse> updateInventoryFortCart(InventoryRequest request);

    ApiResponse<InventoryResponse> confirmOrder(InventoryRequest request);

    ApiResponse<InventoryResponse> cancelOrder(InventoryRequest request);

    int getQuantity(String skuCode);

    Map<String, Integer> extractSkuCodes(List<String> skuCodes);
}
