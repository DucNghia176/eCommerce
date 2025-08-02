package ecommerce.paymentservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.paymentservice.dto.request.PaymentRequest;
import ecommerce.paymentservice.dto.response.PaymentResponse;
import ecommerce.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/confirm")
    ApiResponse<PaymentResponse> confirm(@RequestBody PaymentRequest request) {
        return paymentService.confirmPayment(request);
    }
}
