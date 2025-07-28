package ecommerce.cartservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.CartItemResponse;
import ecommerce.aipcommon.model.response.CartResponse;
import ecommerce.cartservice.dto.request.CartRequest;
import ecommerce.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @PostMapping("/create")
    public ApiResponse<CartResponse> createCart(@RequestBody CartRequest request) {
        return cartService.addProductToCart(request);
    }

    @PutMapping("/update")
    public ApiResponse<CartResponse> updateCart(@RequestBody CartRequest request) {
        return cartService.updateProduct(request);
    }

    @GetMapping
    public ApiResponse<CartResponse> getCartByUserId() {
        return cartService.getCarByUser();
    }

    @DeleteMapping("/remove/{id}")
    public ApiResponse<Void> removeCart(@PathVariable Long id) {
        return cartService.removeProductFromCart(id);
    }

    @PostMapping("/clear")
    public ApiResponse<Void> clearSelectedItemsFromCart(@RequestBody List<Long> ids) {
        return cartService.clearSelectedItemsFromCart(ids);
    }

    @PostMapping("/selected-items")
    public List<CartItemResponse> getSelectedCartItem(@RequestBody Map<Long, Boolean> items) {
        return cartService.getSelectedCartItem(items);
    }

}
