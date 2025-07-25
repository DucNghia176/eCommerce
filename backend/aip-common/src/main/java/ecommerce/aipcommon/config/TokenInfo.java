package ecommerce.aipcommon.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenInfo {

    @Autowired
    private HttpServletRequest request;

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
