package ecommerce.cartservice.client;

import ecommerce.aipcommon.client.UserClient;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service", contextId = "cartUserClient", path = "/users")
public interface CartUserClient extends UserClient {
}
