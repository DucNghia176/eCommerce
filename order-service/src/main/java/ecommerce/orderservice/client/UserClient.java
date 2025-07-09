package ecommerce.orderservice.client;

import ecommerce.aipcommon.model.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        path = "/users",
        configuration = ecommerce.orderservice.config.FeignConfig.class // Gắn config ở đây
)
public interface UserClient {
    @GetMapping("/{id}")
    UserResponse getUsersById(@PathVariable("id") Long id);
}
