package ecommerce.inventoryservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.inventoryservice.dto.request.InventoryRequest;
import ecommerce.inventoryservice.dto.response.InventoryResponse;
import ecommerce.inventoryservice.entity.Inventory;

import java.util.Optional;

public interface InventoryService {
    public boolean isInStock(InventoryRequest request);

    Optional<Inventory> findBySkuCode(String skuCode);

    ApiResponse<InventoryResponse> importQuantity(InventoryRequest request);

    ApiResponse<InventoryResponse> updateInventoryFortCart(InventoryRequest request);

    ApiResponse<InventoryResponse> confirmOrder(InventoryRequest request);

    ApiResponse<InventoryResponse> cancelOrder(InventoryRequest request);

}
