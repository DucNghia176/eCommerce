package ecommerce.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import java.util.List;

@Configuration
public class BeanConfig {
    private static final String ALLOWED_HEADERS = "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN";
    private static final String ALLOWED_METHODS = "GET, PUT, POST, DELETE, OPTIONS";
    private static final String ALLOWED_ORIGIN = "*";
    private static final String MAX_AGE = "3600";
    private static final List<String> WHITELIST = List.of(
            "http://localhost:4200",
            "http://127.0.0.1:4200"
    );


    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            ServerHttpRequest request = ctx.getRequest();
            ServerHttpResponse response = ctx.getResponse();

            String origin = request.getHeaders().getOrigin();

            // Kiểm tra origin có nằm trong WHITELIST không
            if (origin != null && WHITELIST.contains(origin)) {

                HttpHeaders headers = response.getHeaders();
                headers.set("Access-Control-Allow-Origin", origin);
                headers.add("Access-Control-Allow-Credentials", "true");
                headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
                headers.add("Access-Control-Allow-Headers", ALLOWED_HEADERS);
                headers.add("Access-Control-Max-Age", MAX_AGE);
                headers.add("Access-Control-Expose-Headers", "*");
            }

            // Request CORS Pre-flight (OPTIONS)
            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return response.setComplete();
            }

            return chain.filter(ctx);
        };
    }

//    @Bean
//    public WebFilter corsFilter() {
//        return (ServerWebExchange ctx, WebFilterChain chain) -> {
//            ServerHttpRequest request = ctx.getRequest();
//            ServerHttpResponse response = ctx.getResponse();
//
//            response.getHeaders().add("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
//            response.getHeaders().add("Access-Control-Allow-Methods", ALLOWED_METHODS);
//            response.getHeaders().add("Access-Control-Allow-Headers", ALLOWED_HEADERS);
//            response.getHeaders().add("Access-Control-Max-Age", MAX_AGE);
//
//            if (request.getMethod() == HttpMethod.OPTIONS) {
//                response.setStatusCode(HttpStatus.OK);
//                return response.setComplete();
//            }
//
//            return chain.filter(ctx);
//        };
//    }

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
