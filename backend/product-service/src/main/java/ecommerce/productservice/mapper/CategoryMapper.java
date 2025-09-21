package ecommerce.productservice.mapper;

import ecommerce.productservice.dto.request.CategoryRequest;
import ecommerce.productservice.dto.response.CategoryResponse;
import ecommerce.productservice.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "1")
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "isVisible", ignore = true)
    Category toEntity(CategoryRequest request);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(target = "productCount", ignore = true)
    CategoryResponse toResponse(Category category);
}
