package ecommerce.paymentservice.dto.response;

import ecommerce.apicommon1.model.status.PaymentStatus;

public interface OrderIdPaymentStatus {
    Long getOrderId();

    PaymentStatus getStatus();
}
