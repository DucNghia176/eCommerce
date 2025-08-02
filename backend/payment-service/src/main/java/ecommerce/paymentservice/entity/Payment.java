package ecommerce.paymentservice.entity;

import ecommerce.aipcommon.model.status.PaymentMethodStatus;
import ecommerce.aipcommon.model.status.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PAYMENT", schema = "order_db")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq")
    @SequenceGenerator(name = "payment_seq", sequenceName = "SEQ_PAYMENT_ID", allocationSize = 1)
    @Column(name = "PAYMENT_ID")
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "ORDER_CODE")
    private String orderCode;

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

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}
