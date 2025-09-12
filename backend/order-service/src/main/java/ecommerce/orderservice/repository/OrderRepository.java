package ecommerce.orderservice.repository;

import ecommerce.aipcommon.model.response.UserOrderDetailResponse;
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

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    @Query("select new ecommerce.orderservice.dto.response.OrdersAD(o.id, o.orderCode, o.orderDate, o.userId, o.status, o.totalAmount, o.paymentMethod) " +
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
            SELECT new ecommerce.aipcommon.model.response.UserOrderDetailResponse(o.id, o.orderCode , o.orderDate, o.status, o.totalAmount)
                        FROM Orders o
                        WHERE o.userId = :userId
            """)
    List<UserOrderDetailResponse> findOrdersDetailByUserId(@Param("userId") Long userId);
}
