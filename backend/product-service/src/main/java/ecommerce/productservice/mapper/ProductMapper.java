package ecommerce.productservice.mapper;

import ecommerce.productservice.dto.request.ProductRequest;
import ecommerce.productservice.dto.response.ProductResponse;
import ecommerce.productservice.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "1")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    Product toEntity(ProductRequest request);


    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(source = "price", target = "price")
    @Mapping(source = "discountPrice", target = "discountPrice")
    ProductResponse toResponse(Product product);
}
