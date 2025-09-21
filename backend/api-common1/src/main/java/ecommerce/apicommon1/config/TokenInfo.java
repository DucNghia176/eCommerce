package ecommerce.apicommon1.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TokenInfo {
    private final HttpServletRequest request;

    public Long getUserId() {
        String idHeader = request.getHeader("X-User-Id");
        if (idHeader != null && !idHeader.isEmpty()) {
            return Long.parseLong(idHeader);
        }
        return null;
    }

    public String getUsername() {
        return request.getHeader("X-Username");
    }

    public List<String> getRoles() {
        String rolesHeader = request.getHeader("X-Role");
        if (rolesHeader != null && !rolesHeader.isEmpty()) {
            return Arrays.stream(rolesHeader.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public boolean hasRole(String role) {
        return getRoles().contains(role);
    }

//     * Lấy Authentication hiện tại nếu cần

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    //     * Lấy authorities từ SecurityContext
    public List<GrantedAuthority> getAuthorities() {
        Authentication auth = getAuthentication();
        return auth != null && auth.getAuthorities() != null
                ? auth.getAuthorities().stream().collect(Collectors.toList())
                : Collections.emptyList();
    }

}
