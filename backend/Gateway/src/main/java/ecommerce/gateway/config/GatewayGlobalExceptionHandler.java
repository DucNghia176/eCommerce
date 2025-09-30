package ecommerce.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.gateway.dto.ApiResponse;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GatewayGlobalExceptionHandler implements WebExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        // Nếu response đã commit, không thể ghi
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // Chuẩn hóa ApiResponse
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(getStatusCode(ex))
                .message(getMessage(ex))
                .data(null)
                .build();

        response.setStatusCode(HttpStatus.valueOf(apiResponse.getCode()));
        response.getHeaders().add("Content-Type", "application/json");

        DataBuffer buffer;
        try {
            buffer = response.bufferFactory()
                    .wrap(objectMapper.writeValueAsBytes(apiResponse));
        } catch (Exception e) {
            return Mono.error(e);
        }

        return response.writeWith(Mono.just(buffer));
    }

    private int getStatusCode(Throwable ex) {
        if (ex instanceof org.springframework.web.reactive.function.client.WebClientResponseException wce) {
            return wce.getRawStatusCode();
        }
        // Nếu lỗi liên quan đến không tìm thấy instance service, trả 503
        String msg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
        if (msg.contains("unable to find instance") || msg.contains("service unavailable")) {
            return HttpStatus.SERVICE_UNAVAILABLE.value();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    private String getMessage(Throwable ex) {
        if (ex instanceof org.springframework.web.reactive.function.client.WebClientResponseException wce) {
            return "Lỗi từ dịch vụ: " + wce.getResponseBodyAsString();
        }
        String msg = ex.getMessage() != null ? ex.getMessage() : "";
        if (msg.toLowerCase().contains("unable to find instance") || msg.toLowerCase().contains("service unavailable")) {
            return "Dịch vụ tạm thời không khả dụng, vui lòng thử lại sau";
        }
        return "Lỗi tại Gateway: " + msg;
    }
}
