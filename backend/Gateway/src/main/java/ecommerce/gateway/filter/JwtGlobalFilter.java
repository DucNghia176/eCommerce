package ecommerce.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.gateway.dto.ApiResponse;
import ecommerce.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
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
            "/api/cartNodejs"
    );

    //    private final TokenBlacklistService tokenBlacklistService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

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
                    return this.writeForbiddenResponse(exchange, "Bạn không có quyền truy cập tài nguyên này");
                }

                // Gắn thông tin user vào header để gửi xuống các microservice
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", userId != null ? userId.toString() : "")
                        .header("X-Username", username)
                        .header("X-Role", roleHeader)
                        .build();
                exchange = exchange.mutate().request(mutatedRequest).build();

                log.info("Header X-User-Id: {}", userId);
                log.info("Header X-Role: {}", roleHeader);

            } catch (Exception e) {
                log.warn("Token không hợp lệ: {}", e.getMessage());
                return this.writeUnauthorizedResponse(exchange, "Token không hợp lệ hoặc đã hết hạn");
            }
        } else {
            return this.writeUnauthorizedResponse(exchange, "Thiếu token hoặc token không hợp lệ");
        }

        return chain.filter(exchange);
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
