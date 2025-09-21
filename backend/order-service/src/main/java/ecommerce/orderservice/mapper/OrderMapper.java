package ecommerce.orderservice.mapper;

import ecommerce.orderservice.dto.request.OrderRequest;
import ecommerce.orderservice.dto.response.OrderCreateResponse;
import ecommerce.orderservice.dto.response.OrderResponse;
import ecommerce.orderservice.entity.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "orderCode", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "orderDetails", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Orders toOrderEntity(OrderRequest request);

    @Mapping(target = "status", expression = "java(orders.getStatus() != null ? orders.getStatus().name() : null)")
    @Mapping(source = "orderDetails", target = "orderDetails")
    OrderResponse toResponse(Orders orders);


    @Mapping(target = "status", expression = "java(orders.getStatus() != null ? orders.getStatus().name() : null)")
    @Mapping(source = "orderDetails", target = "orderDetails")
    OrderCreateResponse toDto(Orders orders);
}
