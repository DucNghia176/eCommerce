package ecommerce.paymentservice.entity;

import ecommerce.aipcommon.model.status.PaymentMethodStatus;
import ecommerce.aipcommon.model.status.PaymentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_ID", nullable = false)
    private Long id;

    @Column(name = "ORDER_ID", nullable = false)
    private Long orderId;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "PAYMENT_DATE")
    private Instant paymentDate;

    @Column(name = "AMOUNT_PAID")
    private BigDecimal amountPaid;


    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_METHOD")
    private PaymentMethodStatus paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private PaymentStatus status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}
