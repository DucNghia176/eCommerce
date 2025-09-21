package ecommerce.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service", path = "/api/users",
        configuration = ecommerce.apicommon1.config.FeignConfig.class)
public interface UserClient {
    @GetMapping("/ids")
    Map<Long, String> extractFullName(@RequestParam("ids") List<Long> ids);
}
