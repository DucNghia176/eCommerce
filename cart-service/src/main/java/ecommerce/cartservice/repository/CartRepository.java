package ecommerce.cartservice.repository;

import ecommerce.cartservice.entity.CartItem;
import ecommerce.cartservice.entity.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartItem, CartItemId> {
    List<CartItem> findByIdUserId(Long userId);

    void deleteByIdUserIdAndIsSelected(Long userId, Integer isSelected);

    List<CartItem> findByIdUserIdAndIsSelected(Long userId, Integer isSelected);

}
