package ecommerce.orderservice.util;

import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.aipcommon.util.JwtUtil;
import ecommerce.orderservice.client.OrderUserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderValidator {
    private final OrderUserClient orderUserClient;
    private final JwtUtil jwtUtil;

    public Long extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Thiếu hoặc sai định dạng Authorization header");
        }
        return jwtUtil.extractId(authHeader.replace("Bearer ", "").trim());
    }

    public UserResponse validateUser(Long userId) {
        try {
            return orderUserClient.getUsersById(userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Người dùng ko tồn tại với ID = " + userId);
        }
    }
}
