package ecommerce.paymentservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.status.PaymentStatus;
import ecommerce.paymentservice.dto.request.PaymentRequest;
import ecommerce.paymentservice.dto.response.PaymentResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    ApiResponse<PaymentResponse> confirmPayment(PaymentRequest request);

    Map<Long, PaymentStatus> extractStatus(List<Long> orderIds);

    Map<Long, BigDecimal> extractAmount(List<Long> userIds);
}
