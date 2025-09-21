package ecommerce.cartservice.controller;

import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.response.CartItemResponse;
import ecommerce.apicommon1.model.response.CartResponse;
import ecommerce.cartservice.dto.request.CartRequest;
import ecommerce.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/cart")
@PreAuthorize("hasAuthority('USER')")
public class CartController {
    private final CartService cartService;

    @PostMapping("/create")
    public ApiResponse<CartResponse> createCart(@RequestBody CartRequest request) {
        return cartService.addProductToCart(request);
    }

    @PutMapping("/update")
    public ApiResponse<CartResponse> updateCart(@RequestBody CartRequest request) {
        return cartService.updateCartProduct(request);
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
    public ApiResponse<List<CartItemResponse>> getSelectedCartItem(@RequestBody Map<Long, Boolean> items) {
        return cartService.getSelectedCartItem(items);
    }
}
