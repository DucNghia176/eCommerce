package ecommerce.gateway.filter;

import ecommerce.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    private final static List<String> API_PUBLIC = List.of(
            "/api/auth",
            "/api/product/search",
            "/api/brand",
            "/api/category"

    );

    //    private final TokenBlacklistService tokenBlacklistService;
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        boolean isPublic = API_PUBLIC.stream().anyMatch(publicPath -> path.equals(publicPath)
                || path.startsWith(publicPath + "/"));

        if (isPublic) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                List<String> roles = jwtUtil.extractRoles(token);
                String username = jwtUtil.extractUsername(token);
                Long userId = jwtUtil.extractUserId(token);

                String roleHeader = String.join(",", roles);

                // Nếu path chứa /admin mà role không phải ADMIN thì chặn
                if (path.contains("/admin") && !roles.contains("ADMIN")) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                // Gắn thông tin user vào header để gửi xuống các microservice
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", userId != null ? userId.toString() : "")
                        .header("X-Username", username)
                        .header("X-Role", roleHeader)
                        .build();
                exchange = exchange.mutate().request(mutatedRequest).build();

            } catch (Exception e) {
                log.warn("Token không hợp lệ: {}", e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
