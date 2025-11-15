package ecommerce.orderservice.repository;

import ecommerce.apicommon1.model.response.UserOrderDetailResponse;
import ecommerce.apicommon1.model.status.OrderStatus;
import ecommerce.orderservice.dto.response.OrderQuantityResponse;
import ecommerce.orderservice.dto.response.OrdersAD;
import ecommerce.orderservice.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    @Query("select new ecommerce.orderservice.dto.response.OrdersAD(o.id, o.orderCode, o.createdAt, o.userId, o.status, o.totalAmount, o.paymentMethod) " +
            "from Orders o")
    Page<OrdersAD> getAll(Pageable pageable);


    @Query("""
            SELECT new ecommerce.orderservice.dto.response.OrderQuantityResponse(o.userId, COUNT(o.id))
                        FROM Orders o
                        WHERE o.userId IN :usersId
                        GROUP BY o.userId
            """)
    List<OrderQuantityResponse> countOrdersByUserIds(@Param("usersId") List<Long> usersId);

    @Query("""
            SELECT new ecommerce.apicommon1.model.response.UserOrderDetailResponse(o.id, o.orderCode , o.createdAt, o.status, o.totalAmount)
                        FROM Orders o
                        WHERE o.userId = :userId
            """)
    List<UserOrderDetailResponse> findOrdersDetailByUserId(@Param("userId") Long userId);


    @Query("SELECT CASE WHEN COUNT(od) > 0 THEN true ELSE false END " +
            "FROM Orders o JOIN o.orderDetails od " +
            "WHERE o.userId = :userId " +
            "AND od.productId = :productId " +
            "AND o.status = :status")
    boolean existsByUserIdAndProductIdAndStatus(@Param("userId") Long userId,
                                                @Param("productId") Long productId,
                                                @Param("status") OrderStatus status);

    Optional<Orders> findByUserIdAndCartSignature(Long userId, String cartSignature);

    List<Orders> findOrdersByUserId(Long userId);

    Orders findOrdersByOrderCode(String orderCode);
}
