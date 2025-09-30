package ecommerce.productservice.mapper;

import ecommerce.productservice.dto.request.CreateProductRequest;
import ecommerce.productservice.dto.response.ProductAttributeResponse;
import ecommerce.productservice.dto.response.ProductResponse;
import ecommerce.productservice.dto.response.TagResponse;
import ecommerce.productservice.entity.Product;
import ecommerce.productservice.entity.ProductAttribute;
import ecommerce.productservice.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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
    Product toEntity(CreateProductRequest request);


    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "tags", target = "tags", qualifiedByName = "mapTags")
    @Mapping(target = "imageUrls", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(source = "skuCode", target = "skuCode")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "discount", target = "discountPrice")
    @Mapping(source = "productAttributes", target = "attributes", qualifiedByName = "mapAttributes")
    ProductResponse toResponse(Product product);

    default Set<Tag> mapTags(List<Long> tagIds) {
        if (tagIds == null) return null;
        return tagIds.stream()
                .map(id -> {
                    Tag tag = new Tag();
                    tag.setId(id);
                    return tag;
                })
                .collect(Collectors.toSet());
    }

    @Named("mapTags")
    default List<TagResponse> mapTags(Set<Tag> tags) {
        if (tags == null) return List.of();
        return tags.stream()
                .map(tag -> TagResponse.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Named("mapAttributes")
    default List<ProductAttributeResponse> mapAttributes(Set<ProductAttribute> productAttributes) {
        if (productAttributes == null) return List.of();
        return productAttributes.stream()
                .collect(Collectors.groupingBy(
                        pa -> pa.getAttribute().getName(),
                        Collectors.mapping(pa -> pa.getValue().getValue(), Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> ProductAttributeResponse.builder()
                        .attribute(entry.getKey())
                        .values(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }
}
