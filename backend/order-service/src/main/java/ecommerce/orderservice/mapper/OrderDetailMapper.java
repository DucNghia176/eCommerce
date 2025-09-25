package ecommerce.orderservice.mapper;

import ecommerce.orderservice.dto.request.OrderDetailRequest;
import ecommerce.orderservice.dto.response.OrderDetailResponse;
import ecommerce.orderservice.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderDetail toEntity(OrderDetailRequest request);

    OrderDetailResponse toResponse(OrderDetail orderDetails);
}
