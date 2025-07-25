package ecommerce.cartservice.config;

import ecommerce.cartservice.entity.Cart;
import ecommerce.cartservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("cartSecurity")
public class CartConfig {
    @Autowired
    private CartRepository cartRepository;

    public Long getUserIdByCartId(Long cartId) {
        return cartRepository.findById(cartId)
                .map(Cart::getUserId)
                .orElse(null);
    }
}
