package ecommerce.paymentservice.dto.response;

import ecommerce.aipcommon.model.status.PaymentStatus;

public interface OrderIdPaymentStatus {
    Long getOrderId();

    PaymentStatus getStatus();
}
