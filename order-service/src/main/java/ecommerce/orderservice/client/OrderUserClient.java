package ecommerce.orderservice.client;

import ecommerce.aipcommon.client.UserClient;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service", contextId = "orderUserClient", path = "/users")
public interface OrderUserClient extends UserClient {
}
