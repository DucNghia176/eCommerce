package ecommerce.paymentservice.kafka.listener;

import ecommerce.aipcommon.kafka.event.PaymentKafkaEvent;
import ecommerce.aipcommon.model.status.PaymentStatus;
import ecommerce.paymentservice.entity.Payment;
import ecommerce.paymentservice.repostitory.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentKafkaListener {
    private final PaymentRepository paymentRepository;

    @KafkaListener(topics = "payment-topic", groupId = "payment-group")
    public void handleOrderPlace(PaymentKafkaEvent event) {
        log.info("📦 Nhận event tạo thanh toán: {}", event);
        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .orderCode(event.getOrderCode())
                .userId(event.getUserId())
                .amountPaid(event.getTotalAmount())
                .paymentMethod(event.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        log.info("Đã lưu kho cho orderCode: {}", event.getOrderCode());
    }
}