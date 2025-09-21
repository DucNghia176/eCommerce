package ecommerce.paymentservice.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import ecommerce.apicommon1.kafka.event.PaymentKafkaEvent;
import ecommerce.apicommon1.model.request.UpdateOrderStatusRequest;
import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.status.OrderStatus;
import ecommerce.apicommon1.model.status.PaymentStatus;
import ecommerce.paymentservice.client.OrderClient;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final KafkaTemplate<String, PaymentKafkaEvent> kafkaTemplate;
    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

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

    @Override
    public String createCheckoutSession(Long orderId, BigDecimal amount) throws StripeException {
        BigDecimal exchangeRate = new BigDecimal("0.000043"); // 1 VND ≈ 0.000043 USD
        Long amountInCents = amount.multiply(exchangeRate)
                .multiply(BigDecimal.valueOf(100)) // đổi USD → cents
                .longValue();
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:4200/success?orderId=" + orderId)
                        .setCancelUrl("http://localhost:4200/cancel")
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("vnd")
                                                        .setUnitAmount(amountInCents)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Order #" + orderId)
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    @Transactional
    @Override
    public void confirmPayment(Long orderId) throws StripeException {
        Payment payment = paymentRepository.findByOrderId1(orderId);
        if (payment == null) {
            throw new NoSuchElementException("Payment not found");
        }

        // Kiểm tra Stripe payment status
        PaymentIntent intent = PaymentIntent.retrieve(payment.getStripePaymentIntentId());

        if ("succeeded".equals(intent.getStatus())) {
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            // Update trạng thái order
            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                    .orderId(orderId)
                    .orderStatus(OrderStatus.CONFIRMED)
                    .build();
            orderClient.updateOrderStatus(request);

        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }
    }

    public PaymentStatus getPaymentStatus(Long orderId) {
        Payment payment = paymentRepository.findByOrderId1(orderId);
        if (payment == null) return null;
        return payment.getStatus();
    }
}
