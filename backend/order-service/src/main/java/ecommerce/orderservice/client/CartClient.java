package ecommerce.orderservice.client;

import ecommerce.aipcommon.config.FeignConfig;
import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.orderservice.dto.response.CartItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "cart-service", path = "/cart", configuration = FeignConfig.class)
public interface CartClient {
    @PostMapping("/clear")
    ApiResponse<Void> clearSelectedCartItems(@RequestBody List<Long> itemId);

    @PostMapping("/selected-items")
    List<CartItemResponse> getSelectedCartItem(@RequestBody Map<Long, Boolean> items);
}

