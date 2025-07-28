package ecommerce.cartservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.CartItemResponse;
import ecommerce.aipcommon.model.response.CartResponse;
import ecommerce.cartservice.dto.request.CartRequest;

import java.util.List;
import java.util.Map;

public interface CartService {
    ApiResponse<CartResponse> addProductToCart(CartRequest request);

    ApiResponse<CartResponse> updateProduct(CartRequest request);

    ApiResponse<CartResponse> getCarByUser();

    ApiResponse<Void> removeProductFromCart(Long productId);

    ApiResponse<Void> clearSelectedItemsFromCart(List<Long> ids);

    List<CartItemResponse> getSelectedCartItem(Map<Long, Boolean> items);
}