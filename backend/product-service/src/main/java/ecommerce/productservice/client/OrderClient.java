package ecommerce.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", path = "/api/orders")
public interface OrderClient {
    @GetMapping("/exists")
    boolean hasPurchased(@RequestParam Long userId, @RequestParam Long productId);
}
