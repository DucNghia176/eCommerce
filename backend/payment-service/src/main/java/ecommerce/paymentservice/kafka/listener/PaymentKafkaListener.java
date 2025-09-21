package ecommerce.paymentservice.kafka.listener;

import ecommerce.apicommon1.kafka.event.PaymentKafkaEvent;
import ecommerce.apicommon1.model.status.PaymentMethodStatus;
import ecommerce.apicommon1.model.status.PaymentStatus;
import ecommerce.paymentservice.entity.Payment;
import ecommerce.paymentservice.repostitory.PaymentRepository;
import ecommerce.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentKafkaListener {
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

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

        if (!event.getPaymentMethod().equals(PaymentMethodStatus.COD)) {
            try {
                String checkoutUrl = paymentService.createCheckoutSession(event.getOrderId(), event.getTotalAmount());
                log.info("🟢 Checkout URL: {}", checkoutUrl);
            } catch (Exception e) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }
        }
    }
}