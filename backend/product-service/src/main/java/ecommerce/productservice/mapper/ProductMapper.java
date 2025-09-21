package ecommerce.productservice.mapper;

import ecommerce.productservice.dto.request.ProductRequest;
import ecommerce.productservice.dto.response.ProductResponse;
import ecommerce.productservice.entity.Product;
import ecommerce.productservice.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Mapping(source = "skuCode", target = "skuCode")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "discount", target = "discountPrice")
    ProductResponse toResponse(Product product);

    default Set<Tag> map(List<Long> tagIds) {
        if (tagIds == null) return null;
        return tagIds.stream()
                .map(id -> {
                    Tag tag = new Tag();
                    tag.setId(id);
                    return tag;
                })
                .collect(Collectors.toSet());
    }
}
