package ecommerce.paymentservice.service.impl;

import ecommerce.aipcommon.kafka.event.PaymentKafkaEvent;
import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.status.PaymentStatus;
import ecommerce.paymentservice.dto.request.PaymentRequest;
import ecommerce.paymentservice.dto.response.OrderIdPaymentStatus;
import ecommerce.paymentservice.dto.response.PaymentResponse;
import ecommerce.paymentservice.dto.response.TotalAmountByUserId;
import ecommerce.paymentservice.entity.Payment;
import ecommerce.paymentservice.repostitory.PaymentRepository;
import ecommerce.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final KafkaTemplate<String, PaymentKafkaEvent> kafkaTemplate;
    private final PaymentRepository paymentRepository;

    @Override
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

    @Override
    public Map<Long, PaymentStatus> extractStatus(List<Long> orderIds) {
        List<OrderIdPaymentStatus> payments = paymentRepository.findByOrderIdIn(orderIds);

        Map<Long, PaymentStatus> responses = payments.stream()
                .collect(Collectors.toMap(OrderIdPaymentStatus::getOrderId, OrderIdPaymentStatus::getStatus));

        return responses;
    }

    @Override
    public Map<Long, BigDecimal> extractAmount(List<Long> userIds) {
        List<TotalAmountByUserId> payments = paymentRepository.totalAmountByUserIds(userIds);

        Map<Long, BigDecimal> response = payments.stream()
                .collect(Collectors.toMap(TotalAmountByUserId::getUserId, TotalAmountByUserId::getTotalAmount));

        return response;
    }
}
