package ecommerce.orderservice.client;

import ecommerce.apicommon1.model.response.ProductPriceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "product-service", path = "/api/product")
public interface ProductClient {
    @GetMapping("/{id}/skuCode")
    String getSkuCode(@PathVariable("id") Long id);

    @PostMapping("exists")
    Map<Long, Boolean> checkProduct(@RequestBody List<Long> ids);

    @GetMapping("/price/{id}")
    ProductPriceResponse productPrice(@PathVariable Long id);
}
