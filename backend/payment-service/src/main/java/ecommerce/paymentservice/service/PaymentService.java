package ecommerce.paymentservice.service;

import ecommerce.aipcommon.kafka.event.PaymentKafkaEvent;
import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.paymentservice.dto.request.PaymentRequest;
import ecommerce.paymentservice.dto.response.PaymentResponse;
import ecommerce.paymentservice.entity.Payment;
import ecommerce.paymentservice.repostitory.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
    private final KafkaTemplate<String, PaymentKafkaEvent> kafkaTemplate;
    private final PaymentRepository paymentRepository;

    public ApiResponse<PaymentResponse> confirmPayment(PaymentRequest request) {
        try {
            String orderCode = paymentRepository.findOrderCodeByOrderId(request.getOrderId());

            PaymentKafkaEvent event = PaymentKafkaEvent.builder()
                    .orderCode(orderCode)
                    .orderId(request.getOrderId())
                    .timestamp(LocalDateTime.now())
                    .status(request.getPaymentStatus())
                    .build();

            Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                    .orElseGet(() -> Payment.builder()
                            .orderId(request.getOrderId())
                            .orderCode(orderCode)
                            .createdAt(LocalDateTime.now())
                            .build());

            payment.setStatus(request.getPaymentStatus());
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            kafkaTemplate.send("payment-confirm", event);

            PaymentResponse response = PaymentResponse.builder()
                    .orderId(request.getOrderId())
                    .orderCode(orderCode)
                    .status(request.getPaymentStatus().toString())
                    .build();

            return ApiResponse.<PaymentResponse>builder()
                    .code(200)
                    .message("Đã xác nhận thanh toán và gửi Kafka")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResponse.<PaymentResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống" + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}
