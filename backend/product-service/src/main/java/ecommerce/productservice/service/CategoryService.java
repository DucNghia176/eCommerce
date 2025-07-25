package ecommerce.productservice.service;


import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.productservice.dto.request.CategoryRequest;
import ecommerce.productservice.dto.response.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    ApiResponse<CategoryResponse> createCategory(CategoryRequest request);

    ApiResponse<CategoryResponse> updateCategory(Long id, CategoryRequest request);

    ApiResponse<List<CategoryResponse>> getAllCategory();

    ApiResponse<CategoryResponse> deleteCategory(Long id);

}
