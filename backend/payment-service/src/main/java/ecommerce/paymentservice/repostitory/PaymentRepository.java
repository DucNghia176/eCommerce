package ecommerce.paymentservice.repostitory;

import ecommerce.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p.orderCode FROM Payment p WHERE p.orderId = :orderId")
    String findOrderCodeByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT p.orderId FROM Payment p WHERE p.orderCode = :orderCode")
    String findOrderIdByOrderCode(@Param("orderId") Long orderCode);

    Optional<Payment> findByOrderId(Long orderId);
}
