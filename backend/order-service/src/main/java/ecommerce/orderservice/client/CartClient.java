package ecommerce.orderservice.client;

import ecommerce.apicommon1.config.FeignConfig;
import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.response.CartItemResponse;
import ecommerce.apicommon1.model.response.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "cart-service", path = "/api/cart", configuration = FeignConfig.class)
public interface CartClient {

    @GetMapping
    ApiResponse<CartResponse> getCartByUserId();

    @PostMapping("/clear")
    ApiResponse<Void> clearSelectedCartItems(@RequestBody List<Long> itemId);

    @PostMapping("/selected-items")
    ApiResponse<List<CartItemResponse>> getSelectedCartItem(@RequestBody Map<Long, Boolean> items);
}

