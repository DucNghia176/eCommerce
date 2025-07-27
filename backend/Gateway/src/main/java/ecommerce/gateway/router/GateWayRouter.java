package ecommerce.gateway.router;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GateWayRouter {
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("USER-SERVICE", r -> r.path("/users/**", "/auth/**")
                        .uri("lb://USER-SERVICE/"))
                .route("PRODUCT-SERVICE", r -> r.path("/product/**", "/category/**")
                        .uri("lb://PRODUCT-SERVICE/"))
                .route("ORDER-SERVICE", r -> r.path("/orders/**")
                        .uri("lb://ORDER-SERVICE/"))
                .route("CART-SERVICE", r -> r.path("/cart/**")
                        .uri("lb://CART-SERVICE/"))
                .route("INVENTORY-SERVICE", r -> r.path("/inventory/**")
                        .uri("lb://INVENTORY-SERVICE/"))
//                .route("payment-service", r -> r.path("api/payment/**")
//                        .uri("lb://PAYMENT-SERVICE/"))
                .route("notification-service", r -> r.path("/api/notification/**")
                        .uri("lb://NOTIFICATION-SERVICE"))
                .build();
    }
}
