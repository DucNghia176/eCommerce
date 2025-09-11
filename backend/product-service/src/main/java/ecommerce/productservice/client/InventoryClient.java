package ecommerce.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "inventory-service", path = "/api/inventory")
public interface InventoryClient {

    @GetMapping("/check")
    boolean isInStock(@RequestParam("skuCode") String skuCode,
                      @RequestParam("quantity") int quantity);

    @GetMapping("/quantity")
    int getQuantity(@RequestParam("skuCode") String skuCode);

    @GetMapping("/quantities")
    Map<String, Integer> getQuantities(@RequestParam("skuCodes") List<String> skuCodes);
}

