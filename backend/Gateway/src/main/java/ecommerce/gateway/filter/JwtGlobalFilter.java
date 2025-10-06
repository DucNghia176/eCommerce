package ecommerce.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.gateway.dto.ApiResponse;
import ecommerce.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    private final static List<String> API_PUBLIC = List.of(
            "/api/auth",
            "/oauth2",
            "/login/oauth2",
            "/api/product/search",
            "/api/brand",
            "/api/category",
            "/api/cartNodejs",
            "/api/product/view",
            "/api/product/view/**"
    );
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final ReactiveStringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        boolean isPublic = API_PUBLIC.stream().anyMatch(publicPath ->
                path.equals(publicPath) || path.startsWith(publicPath + "/")
        );

        if (isPublic) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Kiểm tra token blacklist
            return redisTemplate.hasKey(BLACKLIST_PREFIX + token)
                    .flatMap(isBlacklisted -> {
                        if (Boolean.TRUE.equals(isBlacklisted)) {
                            return writeUnauthorizedResponse(exchange, "Token đã bị thu hồi hoặc logout");
                        }

                        try {
                            // Lấy thông tin từ token
                            var roles = jwtUtil.extractRoles(token);
                            var username = jwtUtil.extractUsername(token);
                            var userId = jwtUtil.extractUserId(token);
                            String roleHeader = String.join(",", roles);

                            // Kiểm tra quyền admin
                            if (path.contains("/admin") && !roles.contains("ADMIN")) {
                                return writeForbiddenResponse(exchange, "Bạn không có quyền truy cập tài nguyên này");
                            }

                            // Gắn thông tin user vào header
                            ServerHttpRequest mutatedRequest = request.mutate()
                                    .header("X-User-Id", userId != null ? userId.toString() : "")
                                    .header("X-Username", username)
                                    .header("X-Role", roleHeader)
                                    .build();

                            return chain.filter(exchange.mutate().request(mutatedRequest).build());

                        } catch (Exception e) {
                            log.warn("Token không hợp lệ: {}", e.getMessage());
                            return writeUnauthorizedResponse(exchange, "Token không hợp lệ hoặc đã hết hạn");
                        }
                    })
                    .onErrorResume(e -> {
                        log.error("Lỗi Redis: {}", e.getMessage());
                        return writeUnauthorizedResponse(exchange, "Không thể xác thực token");
                    });

        } else {
            return writeUnauthorizedResponse(exchange, "Thiếu token hoặc token không hợp lệ");
        }
    }

    private Mono<Void> writeUnauthorizedResponse(ServerWebExchange exchange, String message) {
        return writeJsonResponse(exchange, HttpStatus.UNAUTHORIZED, message);
    }

    private Mono<Void> writeForbiddenResponse(ServerWebExchange exchange, String message) {
        return writeJsonResponse(exchange, HttpStatus.FORBIDDEN, message);
    }

    private Mono<Void> writeJsonResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        ApiResponse<Object> response = ApiResponse.builder()
                .code(status.value())
                .message(message)
                .data(null)
                .build();
        try {
            byte[] bytes = objectMapper.writeValueAsString(response).getBytes(StandardCharsets.UTF_8);
            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
            );
        } catch (Exception e) {
            log.error("Lỗi khi ghi response JSON: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
