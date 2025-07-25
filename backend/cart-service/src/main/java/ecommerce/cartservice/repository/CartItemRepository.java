package ecommerce.cartservice.repository;

import ecommerce.cartservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    void deleteByIdUserIdAndIsSelected(Long userId, Integer isSelected);

    List<CartItem> findByCartIdAndIsSelected(Long userId, Integer isSelected);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
