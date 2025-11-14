package ecommerce.orderservice.client;

import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.response.PaymentIntentResponse;
import ecommerce.apicommon1.model.status.PaymentStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(name = "payment-service", path = "/api/payment",
        configuration = ecommerce.apicommon1.config.FeignConfig.class)
public interface PaymentClient {
    @GetMapping("/orders")
    Map<Long, PaymentStatus> extractPaymentStatus(@RequestParam("orderIds") List<Long> orderIds);

    @GetMapping("/{orderId}")
    ApiResponse<PaymentIntentResponse> createPayment(@PathVariable("orderId") Long orderId,
                                                     @RequestParam("totalAmount") BigDecimal totalAmount);
}
