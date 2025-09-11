package ecommerce.aipcommon.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TokenInfo {
    private final HttpServletRequest request;

    public Long getUserId() {
        String id = request.getHeader("X-User-Id");
        return id != null ? Long.parseLong(id) : null;
    }

    public String getRole() {
        return request.getHeader("X-Role");
    }

    public String getUsername() {
        return request.getHeader("X-Username");
    }
}
