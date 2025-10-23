package ecommerce.gateway.router;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GateWayRouter {
    private static final String[] USER_PATHS = {
            "/api/users/**",
            "/api/auth/**",
            "/oauth2/**",
            "/login/oauth2/**"
    };

    private static final String[] PRODUCT_PATHS = {
            "/api/product/**",
            "/api/category/**",
            "/api/brand/**",
            "/api/tag/**"
    };

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("USER-SERVICE", r -> r.path(USER_PATHS)
                        .uri("lb://USER-SERVICE"))

                .route("PRODUCT-SERVICE", r -> r.path(PRODUCT_PATHS)
                        .uri("lb://PRODUCT-SERVICE"))

                .route("ORDER-SERVICE", r -> r.path("/api/orders/**")
                        .uri("lb://ORDER-SERVICE"))

//                .route("CART-SERVICE", r -> r.path("/api/cart/**")
//                        .uri("lb://CART-SERVICE"))

                .route("CART-SERVICE", r -> r.path("/api/cart/**")
                        .uri("lb://CART-SERVICE"))

                .route("INVENTORY-SERVICE", r -> r.path("/api/inventory/**")
                        .uri("lb://INVENTORY-SERVICE"))

                .route("PAYMENT-SERVICE", r -> r.path("/api/payment/**")
                        .uri("lb://PAYMENT-SERVICE"))

                .route("NOTIFICATION-SERVICE", r -> r.path("/api/notification/**")
                        .uri("lb://NOTIFICATION-SERVICE"))

                .build();
    }
}
