package ecommerce.orderservice.client;

import ecommerce.apicommon.model.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/users")
public interface UserClient {
    @GetMapping("/{id}")
    UserResponse getUsersById(@PathVariable("id") Long id);
}
