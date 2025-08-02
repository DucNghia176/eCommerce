package ecommerce.cartservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "product-service", path = "/api/product")
public interface ProductClient {
    @GetMapping("/{id}/skuCode")
    String getSkuCode(@PathVariable("id") Long id);

    @GetMapping("/{id}/price")
    BigDecimal getPrice(@PathVariable("id") Long id);
}
