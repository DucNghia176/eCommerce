package ecommerce.cartservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.CartResponse;
import ecommerce.aipcommon.model.response.ProductResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.cartservice.dto.request.CartRequest;
import ecommerce.cartservice.entity.CartItem;
import ecommerce.cartservice.entity.CartItemId;
import ecommerce.cartservice.kafka.KafkaCart;
import ecommerce.cartservice.mapper.CartMapper;
import ecommerce.cartservice.repository.CartRepository;
import ecommerce.cartservice.util.CartValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final KafkaCart kafkaCart;
    private final CartMapper cartMapper;
    private final CartValidator validator;

    public ApiResponse<CartResponse> addProductToCart(String authHeader, CartRequest request) {
        try {
            Long userId = validator.extractUserId(authHeader);
            UserResponse user = validator.validateUser(userId);
            ProductResponse product = validator.validateProduct(request.getProductId());
            System.out.println("Product id: " + product.getId());
            System.out.println("product: " + product);
            System.out.println("price: " + product.getPrice());
            System.out.println("discount: " + product.getDiscountPrice());


            CartItemId itemId = new CartItemId(userId, product.getId());
            CartItem existing = cartRepository.findById(itemId).orElse(null);

            CartItem cartItem;

            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + request.getQuantity());
                existing.setUpdatedAt(java.time.LocalDateTime.now());
                cartItem = existing;
            } else if (request.getQuantity() > 0) {
                cartItem = CartItem.builder()
                        .id(itemId)
                        .quantity(request.getQuantity())
                        .unitPrice(product.getPrice())
                        .discount(product.getDiscountPrice())
                        .isSelected(1)
                        .createdAt(java.time.LocalDateTime.now())
                        .build();
            } else {
                return ApiResponse.<CartResponse>builder()
                        .code(400)
                        .message("Số lượng sản phẩm phải lớn hơn 0")
                        .data(null)
                        .build();
            }

            CartItem saved = cartRepository.save(cartItem);
            CartResponse response = cartMapper.toResponse(saved);
            kafkaCart.sendMessage("cart-events", "Thêm sản phẩm vào giỏ hàng ID= " + request.getProductId() + " thành công: ");
            return ApiResponse.<CartResponse>builder()
                    .code(200)
                    .message("Thêm sản phẩm vào giỏ hàng thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi: " + e.getMessage(), e);
            kafkaCart.sendMessage("cart-events", "Thêm giỏ hàng thất bại");
            return ApiResponse.<CartResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }

    public ApiResponse<CartResponse> updateProduct(String authHeader, CartRequest request) {
        try {
            Long userId = validator.extractUserId(authHeader);
            validator.validateUser(userId);
            validator.validateProduct(request.getProductId());
            CartItemId itemId = new CartItemId(userId, request.getProductId());
            CartItem existing = cartRepository.findById(itemId).orElse(null);
            if (existing == null) {
                return ApiResponse.<CartResponse>builder()
                        .code(404)
                        .message("Sản phẩm không tồn tại trong giỏ hàng")
                        .data(null)
                        .build();
            }
            if (request.getQuantity() <= 0) {
                cartRepository.delete(existing);
            }
            existing.setQuantity(request.getQuantity());
            existing.setUpdatedAt(java.time.LocalDateTime.now());
            CartItem saved = cartRepository.save(existing);
            CartResponse response = cartMapper.toResponse(saved);
            kafkaCart.sendMessage("cart-events", "Cập nhật giỏ hàng thành công với userId=" + userId);
            return ApiResponse.<CartResponse>builder()
                    .code(200)
                    .message("Cập nhật giỏ hàng thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi: " + e.getMessage(), e);
            kafkaCart.sendMessage("cart-events", "Cập nhật giỏ hàng thất bại");
            return ApiResponse.<CartResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }

    public ApiResponse<List<CartResponse>> getCarByUser(String authHeader) {
        try {
            Long userId = validator.extractUserId(authHeader);
            validator.validateUser(userId);

            List<CartItem> items = cartRepository.findByIdUserId(userId);
            List<CartResponse> response = items.stream()
                    .map(cartMapper::toResponse)
                    .collect(Collectors.toList());

            return ApiResponse.<List<CartResponse>>builder()
                    .code(200)
                    .message("Lấy giỏ hàng thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi: " + e.getMessage(), e);
            kafkaCart.sendMessage("cart-events", "Cập nhật giỏ hàng thất bại");
            return ApiResponse.<List<CartResponse>>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }

    public ApiResponse<Void> removeProductFromCart(String authHeader, Long productId) {
        try {
            Long userId = validator.extractUserId(authHeader);
            validator.validateUser(userId);
            validator.validateProduct(productId);

            CartItemId itemId = new CartItemId(userId, productId);
            CartItem existing = cartRepository.findById(itemId).orElse(null);
            if (existing == null) {
                return ApiResponse.<Void>builder()
                        .code(404)
                        .message("Sản phẩm không tồn tại trong giỏ hàng")
                        .data(null)
                        .build();
            }

            cartRepository.delete(existing);
            return ApiResponse.<Void>builder()
                    .code(200)
                    .message("Xóa sản phẩm khỏi giỏ hàng thành công")
                    .data(null)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi: " + e.getMessage(), e);
            kafkaCart.sendMessage("cart-events", "Xóa giỏ hàng thất bại");
            return ApiResponse.<Void>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }

    public List<CartResponse> getSelectedCartItems(Long userId) {
        List<CartItem> selectedItems = cartRepository.findByIdUserIdAndIsSelected(userId, 1);
        return selectedItems.stream()
                .map(cartMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ApiResponse<Void> clearSelectedItemsFromCart(String authHeader) {
        try {
            Long userId = validator.extractUserId(authHeader);
            validator.validateUser(userId);

            cartRepository.deleteByIdUserIdAndIsSelected(userId, 1);
            kafkaCart.sendMessage("cart-events", "Xóa các sản phẩm đã chọn trong giỏ hàng thành công");

            return ApiResponse.<Void>builder()
                    .code(200)
                    .message("Đã xóa tất cả sản phẩm đã chọn khỏi giỏ hàng")
                    .data(null)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi: " + e.getMessage(), e);
            kafkaCart.sendMessage("cart-events", "Xóa các sản phẩm đã chọn trong giỏ hàng thất bại");
            return ApiResponse.<Void>builder()
                    .code(500)
                    .message("Lỗi hệ thống khi xóa sản phẩm đã chọn")
                    .data(null)
                    .build();
        }
    }

}
