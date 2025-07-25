package ecommerce.orderservice.client;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "cart-service", path = "/cart")
public interface CartClient {
    @GetMapping("/select")
    List<CartResponse> getSelectedCartItems(@RequestParam("userId") Long userId);

    @DeleteMapping("/clear")
    ApiResponse<Void> clearSelectedCartItems(@RequestHeader("Authorization") String token);
}
