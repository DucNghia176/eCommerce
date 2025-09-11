package ecommerce.orderservice.client;

import ecommerce.aipcommon.model.status.PaymentStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "payment-service", path = "/api/payment",
        configuration = ecommerce.aipcommon.config.FeignConfig.class)
public interface PaymentClient {
    @GetMapping("/orders")
    Map<Long, PaymentStatus> extractPaymentStatus(@RequestParam List<Long> orderIds);
}
