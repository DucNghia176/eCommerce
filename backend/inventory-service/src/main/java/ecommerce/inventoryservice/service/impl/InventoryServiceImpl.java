package ecommerce.inventoryservice.service.impl;

import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.inventoryservice.dto.request.InventoryRequest;
import ecommerce.inventoryservice.dto.response.GetSkuCodeQuantity;
import ecommerce.inventoryservice.dto.response.InventoryResponse;
import ecommerce.inventoryservice.entity.Inventory;
import ecommerce.inventoryservice.repository.InventoryRepository;
import ecommerce.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public ApiResponse<InventoryResponse> importQuantity(InventoryRequest request) {
        try {
            Inventory inventory = inventoryRepository.findById(request.getSkuCode())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
            inventory.setImportedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);
            InventoryResponse response = InventoryResponse.builder()
                    .quantity(inventory.getQuantity())
                    .importPrice(inventory.getImportPrice())
                    .importedAt(inventory.getImportedAt())
                    .build();

            return ApiResponse.<InventoryResponse>builder()
                    .code(200)
                    .message("Thêm số lượng thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<InventoryResponse>builder()
                    .code(500)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<InventoryResponse> updateInventoryFortCart(InventoryRequest request) {
        try {
            Inventory inventory = inventoryRepository.findById(request.getSkuCode())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            InventoryResponse response = InventoryResponse.builder()
                    .skuCode(inventory.getSkuCode())
                    .quantity(inventory.getQuantity())
                    .reservedQuantity(inventory.getReservedQuantity())
                    .build();

            return ApiResponse.<InventoryResponse>builder()
                    .code(200)
                    .message("Có thể thêm vào giỏ hàng")
                    .data(response)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<InventoryResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<InventoryResponse> confirmOrder(InventoryRequest request) {//đătj hàng
        try {
            Inventory inventory = inventoryRepository.findById(request.getSkuCode())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            int availableQuantity = inventory.getQuantity() - inventory.getReservedQuantity();
            inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() + request.getQuantity());

            inventoryRepository.save(inventory);

            InventoryResponse response = InventoryResponse.builder()
                    .skuCode(inventory.getSkuCode())
                    .quantity(inventory.getQuantity())
                    .reservedQuantity(inventory.getReservedQuantity())
                    .build();
            return ApiResponse.<InventoryResponse>builder()
                    .code(200)
                    .message("Đặt hàng thành công. Kho đã được cập nhật.")
                    .data(response)
                    .build();

        } catch (Exception e) {
            return ApiResponse.<InventoryResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<InventoryResponse> cancelOrder(InventoryRequest request) {//hủy
        try {
            Inventory inventory = inventoryRepository.findById(request.getSkuCode())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() - request.getQuantity());

            inventoryRepository.save(inventory);
            InventoryResponse response = InventoryResponse.builder()
                    .skuCode(inventory.getSkuCode())
                    .quantity(inventory.getQuantity())
                    .reservedQuantity(inventory.getReservedQuantity())
                    .build();
            return ApiResponse.<InventoryResponse>builder()
                    .code(200)
                    .message("Đã xử lý trả hàng / huỷ đơn thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<InventoryResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public int getQuantity(String skuCode) {
        try {
            Inventory inventory = inventoryRepository.findById(skuCode).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            return inventory.getQuantity();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Map<String, Integer> extractSkuCodes(List<String> skuCodes) {
        List<GetSkuCodeQuantity> inventories = inventoryRepository.findBySkuCodeIn(skuCodes);

        Map<String, Integer> responses = inventories.stream()
                .collect(Collectors.toMap(GetSkuCodeQuantity::getSkuCode, GetSkuCodeQuantity::getQuantity));

        return responses;
    }
}
