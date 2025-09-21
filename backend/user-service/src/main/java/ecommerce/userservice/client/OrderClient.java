package ecommerce.userservice.client;

import ecommerce.apicommon1.model.response.UserOrderDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "order-service", path = "/api/orders")
public interface OrderClient {
    @GetMapping("/quantity")
    Map<Long, Long> extractOrderQuantity(@RequestParam List<Long> usersId);

    @GetMapping("/{userId}")
    List<UserOrderDetailResponse> findOrdersDetailByUserId(@PathVariable("userId") Long userId);
}
