package ecommerce.cartservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.CartResponse;
import ecommerce.cartservice.dto.request.CartRequest;
import ecommerce.cartservice.service.CartService;
import ecommerce.cartservice.util.CartValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final CartValidator validator;

    @PostMapping("/create")
    public ApiResponse<CartResponse> createCart(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CartRequest request) {
        return cartService.addProductToCart(authHeader, request);
    }

    @PutMapping("/update")
    public ApiResponse<CartResponse> updateCart(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CartRequest request) {
        return cartService.updateProduct(authHeader, request);
    }

    @GetMapping
    public ApiResponse<List<CartResponse>> getCartByUserId(@RequestHeader("Authorization") String authHeader) {
        return cartService.getCarByUser(authHeader);
    }

    @DeleteMapping("/remove/{id}")
    public ApiResponse<Void> removeCart(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long id) {
        return cartService.removeProductFromCart(authHeader, id);
    }

    @GetMapping("/selected")
    List<CartResponse> getSelectedCartItems(@RequestParam("userId") Long userId) {
        return cartService.getSelectedCartItems(userId);
    }

    @DeleteMapping("/clear")
    public ApiResponse<Void> clearSelectedItems(@RequestHeader("Authorization") String token) {
        return cartService.clearSelectedItemsFromCart(token);
    }
}
