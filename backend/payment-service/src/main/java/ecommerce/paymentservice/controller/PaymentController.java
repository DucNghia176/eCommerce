package ecommerce.paymentservice.controller;

import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.status.PaymentStatus;
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

    @PostMapping("/checkout")
    public String checkout(@RequestParam Long orderId, @RequestParam BigDecimal amount) throws Exception {
        return paymentService.createCheckoutSession(orderId, amount);
    }

    @PostMapping("/confirm")
    public ApiResponse<String> confirmPayment(@RequestParam Long orderId) {
        try {
            paymentService.confirmPayment(orderId);
            PaymentStatus status = paymentService.getPaymentStatus(orderId);

            if (PaymentStatus.SUCCESS.equals(status)) {
                return ApiResponse.<String>builder()
                        .code(200)
                        .message("Thanh toán thành công")
                        .data("OK")
                        .build();
            } else {
                return ApiResponse.<String>builder()
                        .code(400)
                        .message("Thanh toán thất bại")
                        .data("FAILED")
                        .build();
            }
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }
}
