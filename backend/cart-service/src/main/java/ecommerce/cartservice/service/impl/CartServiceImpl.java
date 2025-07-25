package ecommerce.cartservice.service.impl;

import ecommerce.aipcommon.config.TokenInfo;
import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.CartResponse;
import ecommerce.aipcommon.model.response.ProductResponse;
import ecommerce.cartservice.dto.request.CartRequest;
import ecommerce.cartservice.entity.Cart;
import ecommerce.cartservice.entity.CartItem;
import ecommerce.cartservice.kafka.KafkaCart;
import ecommerce.cartservice.mapper.CartMapper;
import ecommerce.cartservice.repository.CartItemRepository;
import ecommerce.cartservice.repository.CartRepository;
import ecommerce.cartservice.service.CartService;
import ecommerce.cartservice.util.CartValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final KafkaCart kafkaCart;
    private final CartMapper cartMapper;
    private final CartValidator validator;
    private final TokenInfo tokenInfo;
    private final CartItemRepository cartItemRepository;

    @Override
    public ApiResponse<CartResponse> addProductToCart(CartRequest request) {
        try {
            Long userId = tokenInfo.getUserId();
            if (userId == null) {
                return ApiResponse.<CartResponse>builder()
                        .code(404)
                        .message("Vui lòng đăng nhập")
                        .data(null)
                        .build();
            }

            validator.validateUser(userId);
            ProductResponse product = validator.validateProduct(request.getProductId());

            Cart cart = cartRepository.findByUserId(userId).orElse(null);
            if (cart == null) {
                cart = Cart.builder()
                        .userId(userId)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                cart = cartRepository.save(cart);
            }

            Optional<CartItem> optionalItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

            CartItem cartItem;

            if (optionalItem.isPresent()) {
                cartItem = optionalItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            } else {
                if (request.getQuantity() <= 0) {
                    return ApiResponse.<CartResponse>builder()
                            .code(400)
                            .message("Số lượng sản phẩm phải lớn hơn 0")
                            .data(null)
                            .build();
                }

                cartItem = CartItem.builder()
                        .cart(cart)
                        .productId(product.getId())
                        .quantity(request.getQuantity())
                        .unitPrice(product.getPrice())
                        .discount(product.getDiscountPrice())
                        .isSelected(1)
                        .build();
            }

            cartItemRepository.save(cartItem);

            Cart updatedCart = cartRepository.findById(cart.getId()).orElseThrow();
            CartResponse response = cartMapper.toResponse(updatedCart);

            kafkaCart.sendMessage("cart-events", "Thêm sản phẩm vào giỏ hàng ID= " + product.getId() + " thành công: ");
            return ApiResponse.<CartResponse>builder()
                    .code(200)
                    .message("Thêm sản phẩm vào giỏ hàng thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            kafkaCart.sendMessage("cart-events", "Thêm giỏ hàng thất bại");
            return ApiResponse.<CartResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<CartResponse> updateProduct(CartRequest request) {
        try {
            Long userId = tokenInfo.getUserId();
            validator.validateUser(userId);
            validator.validateProduct(request.getProductId());
            Cart cart = cartRepository.findByUserId(userId)
                    .orElse(null);
            if (cart == null) {
                return ApiResponse.<CartResponse>builder()
                        .code(404)
                        .message("Không tìm thấy giỏ hàng cho người dùng")
                        .data(null)
                        .build();
            }

            Optional<CartItem> optionalItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId());
            if (optionalItem.isEmpty()) {
                return ApiResponse.<CartResponse>builder()
                        .code(404)
                        .message("Sản phẩm không tồn tại trong giỏ hàng")
                        .data(null)
                        .build();
            }
            CartItem cartItem = optionalItem.get();

            // Nếu quantity <= 0 thì xóa khỏi giỏ
            if (request.getQuantity() <= 0) {
                cartItemRepository.delete(cartItem);
            } else {
                cartItem.setQuantity(request.getQuantity());
                cartRepository.save(cart);
                cartItemRepository.save(cartItem);
            }

            // Reload lại giỏ hàng
            cartRepository.findById(cart.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng sau khi cập nhật"));

            CartResponse response = cartMapper.toResponse(cart);

            kafkaCart.sendMessage("cart-events", "Cập nhật giỏ hàng thành công với userId=" + userId);
            return ApiResponse.<CartResponse>builder()
                    .code(200)
                    .message("Cập nhật giỏ hàng thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            kafkaCart.sendMessage("cart-events", "Cập nhật giỏ hàng thất bại");
            return ApiResponse.<CartResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<CartResponse> getCarByUser() {
        try {
            Long userId = tokenInfo.getUserId();
            if (userId == null) {
                return ApiResponse.<CartResponse>builder()
                        .code(401)
                        .message("Vui lòng đăng nhập")
                        .data(null)
                        .build();
            }
            validator.validateUser(userId);

            Cart cart = cartRepository.findByUserId(userId)
                    .orElse(null);

            if (cart == null) {
                return ApiResponse.<CartResponse>builder()
                        .code(404)
                        .message("Không tìm thấy giỏ hàng cho người dùng")
                        .data(null)
                        .build();
            }

            CartResponse response = cartMapper.toResponse(cart);

            return ApiResponse.<CartResponse>builder()
                    .code(200)
                    .message("Lấy giỏ hàng thành công")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            kafkaCart.sendMessage("cart-events", "Cập nhật giỏ hàng thất bại");
            return ApiResponse.<CartResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Void> removeProductFromCart(Long productId) {
        try {
            Long userId = tokenInfo.getUserId();
            if (userId == null) {
                return ApiResponse.<Void>builder()
                        .code(401)
                        .message("Vui lòng đăng nhập")
                        .data(null)
                        .build();
            }
            validator.validateUser(userId);
            validator.validateProduct(productId);

            Cart cart = cartRepository.findByUserId(userId)
                    .orElse(null);
            if (cart == null) {
                return ApiResponse.<Void>builder()
                        .code(404)
                        .message("Không tìm thấy giỏ hàng cho người dùng")
                        .data(null)
                        .build();
            }

            Optional<CartItem> optionalItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
            if (optionalItem.isEmpty()) {
                return ApiResponse.<Void>builder()
                        .code(404)
                        .message("Sản phẩm không tồn tại trong giỏ hàng")
                        .data(null)
                        .build();
            }

            cartItemRepository.delete(optionalItem.get());
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);

            kafkaCart.sendMessage("cart-events", "Xóa sản phẩm ID=" + productId + " khỏi giỏ hàng userId=" + userId + " thành công");

            return ApiResponse.<Void>builder()
                    .code(200)
                    .message("Xóa sản phẩm khỏi giỏ hàng thành công")
                    .data(null)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            kafkaCart.sendMessage("cart-events", "Xóa giỏ hàng thất bại");
            return ApiResponse.<Void>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }

    @Override
    public CartResponse getSelectedCartItems() {
        Long userId = tokenInfo.getUserId();
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) return null;

        // Lọc item được chọn
        List<CartItem> selectedItems = cartItemRepository.findByCartIdAndIsSelected(cart.getId(), 1);

        CartResponse response = cartMapper.toResponse(cart);
        response.setItems(cartMapper.toCartItemResponseList(selectedItems)); // ghi đè lại danh sách items

        return response;
    }


    @Override
    public ApiResponse<Void> clearSelectedItemsFromCart() {
        try {
            Long userId = tokenInfo.getUserId();
            validator.validateUser(userId);

            cartItemRepository.deleteByIdUserIdAndIsSelected(userId, 1);
            kafkaCart.sendMessage("cart-events", "Xóa các sản phẩm đã chọn trong giỏ hàng thành công");

            return ApiResponse.<Void>builder()
                    .code(200)
                    .message("Đã xóa tất cả sản phẩm đã chọn khỏi giỏ hàng")
                    .data(null)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            kafkaCart.sendMessage("cart-events", "Xóa các sản phẩm đã chọn trong giỏ hàng thất bại");
            return ApiResponse.<Void>builder()
                    .code(500)
                    .message("Lỗi hệ thống khi xóa sản phẩm đã chọn")
                    .data(null)
                    .build();
        }
    }

}
