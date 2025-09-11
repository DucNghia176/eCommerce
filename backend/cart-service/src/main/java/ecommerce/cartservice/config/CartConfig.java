package ecommerce.cartservice.config;

import ecommerce.cartservice.entity.Cart;
import ecommerce.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("cartSecurity")
public class CartConfig {
    private final CartRepository cartRepository;

    public Long getUserIdByCartId(Long cartId) {
        return cartRepository.findById(cartId)
                .map(Cart::getUserId)
                .orElse(null);
    }
}
