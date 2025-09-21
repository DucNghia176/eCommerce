package ecommerce.paymentservice.service;

import com.stripe.exception.StripeException;
import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.status.PaymentStatus;
import ecommerce.paymentservice.dto.request.PaymentRequest;
import ecommerce.paymentservice.dto.response.PaymentResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    ApiResponse<PaymentResponse> confirmPayment(PaymentRequest request);

    Map<Long, PaymentStatus> extractStatus(List<Long> orderIds);

    Map<Long, BigDecimal> extractAmount(List<Long> userIds);

    String createCheckoutSession(Long orderId, BigDecimal amount) throws StripeException;

    void confirmPayment(Long orderId) throws StripeException;

    PaymentStatus getPaymentStatus(Long orderId);
}
