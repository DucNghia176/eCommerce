package ecommerce.paymentservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.paymentservice.dto.request.MomoRequest;
import ecommerce.paymentservice.dto.response.MomoResponse;
import ecommerce.paymentservice.service.MomoPaymentService;
import ecommerce.paymentservice.service.PaypalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/momo")
@RequiredArgsConstructor
public class MomoController {

    private final MomoPaymentService momoPaymentService;
    private final PaypalService paypalService;

    @PostMapping("/test")
    public ApiResponse<MomoResponse> testPayment(@RequestBody MomoRequest request) {
        return momoPaymentService.createPaymentUrl(request);
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerId) {
        return "Thanh toán thành công! PaymentID: " + paymentId + ", PayerID: " + payerId;
    }

    @GetMapping("/cancel")
    public String paymentCancel() {
        return "Đã hủy thanh toán.";
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody MomoRequest request) {
        try {
            String approvalUrl = paypalService.createPayment(
                    request.getAmount(),
                    "USD", // hoặc cho người dùng chọn
                    "paypal", // phương thức thanh toán
                    "sale",   // intent
                    "Thanh toán đơn hàng #" + request.getOrderId(),
                    "http://localhost:8085/momo/cancel",
                    "http://localhost:8085/momo/success"
            );
            return ResponseEntity.ok(Map.of("paymentUrl", approvalUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

}