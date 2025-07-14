package ecommerce.cartservice.mapper;

import ecommerce.aipcommon.model.response.CartResponse;
import ecommerce.cartservice.dto.request.CartRequest;
import ecommerce.cartservice.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "id", expression = "java(new CartItemId(userId, request.getProductId()))")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    CartItem toEntity(CartRequest request, Long userId);

    @Mapping(source = "id.userId", target = "userId")
    @Mapping(source = "id.productId", target = "productId")
    CartResponse toResponse(CartItem cartItem);

    List<CartResponse> toResponseList(List<CartItem> cartItems);
}
