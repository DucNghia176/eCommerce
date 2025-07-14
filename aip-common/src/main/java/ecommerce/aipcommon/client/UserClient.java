package ecommerce.aipcommon.client;

import ecommerce.aipcommon.model.response.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface UserClient {
    @GetMapping("/{id}")
    UserResponse getUsersById(@PathVariable("id") Long id);
}
