package ecommerce.cartservice.service;

import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.response.CartItemResponse;
import ecommerce.apicommon1.model.response.CartResponse;
import ecommerce.cartservice.dto.request.CartRequest;

import java.util.List;
import java.util.Map;

public interface CartService {
    ApiResponse<CartResponse> addProductToCart(CartRequest request);

    ApiResponse<CartResponse> updateCartProduct(CartRequest request);

    ApiResponse<CartResponse> getCarByUser();

    ApiResponse<Void> removeProductFromCart(Long productId);

    ApiResponse<Void> clearSelectedItemsFromCart(List<Long> ids);

    ApiResponse<List<CartItemResponse>> getSelectedCartItem(Map<Long, Boolean> items);
}