package ecommerce.paymentservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.status.PaymentStatus;
import ecommerce.paymentservice.dto.request.PaymentRequest;
import ecommerce.paymentservice.dto.response.PaymentResponse;
import ecommerce.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/confirm")
    ApiResponse<PaymentResponse> confirm(@RequestBody PaymentRequest request) {
        return paymentService.confirmPayment(request);
    }

    @GetMapping("/orders")
    Map<Long, PaymentStatus> extractPaymentStatus(@RequestParam List<Long> orderIds) {
        return paymentService.extractStatus(orderIds);
    }

    @GetMapping("/amount")
    Map<Long, BigDecimal> extractAmount(@RequestParam List<Long> userIds) {
        return paymentService.extractAmount(userIds);
    }
}
