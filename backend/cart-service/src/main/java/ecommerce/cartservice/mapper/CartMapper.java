package ecommerce.cartservice.mapper;

import ecommerce.aipcommon.model.response.CartItemResponse;
import ecommerce.aipcommon.model.response.CartResponse;
import ecommerce.cartservice.entity.Cart;
import ecommerce.cartservice.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "unitPrice", target = "unitPrice")
    @Mapping(source = "discount", target = "discount")
    @Mapping(source = "isSelected", target = "isSelected")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    List<CartItemResponse> toCartItemResponseList(List<CartItem> items);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "items", target = "items")
    CartResponse toResponse(Cart cart);
}
