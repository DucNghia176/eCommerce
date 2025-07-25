package ecommerce.cartservice.util;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.ProductResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.aipcommon.util.JwtUtil;
import ecommerce.cartservice.client.CartProductClient;
import ecommerce.cartservice.client.CartUserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartValidator {
    private final CartUserClient userClient;
    private final CartProductClient productClient;
    private final JwtUtil jwtUtil;

    public Long extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Thiếu hoặc sai định dạng Authorization header");
        }
        return jwtUtil.extractId(authHeader.replace("Bearer ", "").trim());
    }

    public UserResponse validateUser(Long userId) {
        try {
            return userClient.getUsersById(userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Người dùng ko tồn tại với ID = " + userId);
        }
    }

    public ProductResponse validateProduct(Long productId) {
        try {
            ApiResponse<ProductResponse> response = productClient.getProductById(productId);
            ProductResponse product = response.getData();
            return product;
        } catch (Exception e) {
            throw new IllegalArgumentException("không tồn tại sản phẩm với ID = " + productId);
        }
    }
}
