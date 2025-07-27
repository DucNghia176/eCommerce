package ecommerce.cartservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.CartResponse;
import ecommerce.cartservice.dto.request.CartRequest;
import ecommerce.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.cloudinary.AccessControlRule.AccessType.token;

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

    @GetMapping("/selected")
    public CartResponse getSelectedCartItems() {
        log.info("Received token: {}", token);
        return cartService.getSelectedCartItems();
    }

    @DeleteMapping("/clear")
    public ApiResponse<Void> clearSelectedItems() {
        return cartService.clearSelectedItemsFromCart();
    }
}
