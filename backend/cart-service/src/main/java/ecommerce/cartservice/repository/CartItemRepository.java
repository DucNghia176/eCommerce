package ecommerce.cartservice.repository;

import ecommerce.cartservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {


//    void deleteByCartItemId(Long id);


    @Query(value = "SELECT * FROM CART_ITEM WHERE ID = :Id", nativeQuery = true)
    List<CartItem> findCartItemById(Long Id);


    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}