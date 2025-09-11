package ecommerce.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(name = "payment-service", path = "/api/payment")
public interface PaymentClient {
    @GetMapping("/amount")
    Map<Long, BigDecimal> extractAmount(@RequestParam List<Long> userIds);
}
