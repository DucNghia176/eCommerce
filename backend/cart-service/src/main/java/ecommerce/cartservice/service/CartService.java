package ecommerce.cartservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.CartResponse;
import ecommerce.cartservice.dto.request.CartRequest;

public interface CartService {
    ApiResponse<CartResponse> addProductToCart(CartRequest request);

    ApiResponse<CartResponse> updateProduct(CartRequest request);

    ApiResponse<CartResponse> getCarByUser();

    ApiResponse<Void> removeProductFromCart(Long productId);

    CartResponse getSelectedCartItems();

    ApiResponse<Void> clearSelectedItemsFromCart();
}