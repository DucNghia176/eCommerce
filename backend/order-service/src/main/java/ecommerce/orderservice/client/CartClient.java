package ecommerce.orderservice.client;

import ecommerce.aipcommon.config.FeignConfig;
import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cart-service", path = "/cart", configuration = FeignConfig.class)
public interface CartClient {
    @GetMapping("/selected")
    CartResponse getSelectedCartItems();

    @DeleteMapping("/clear")
    ApiResponse<Void> clearSelectedCartItems();
}
