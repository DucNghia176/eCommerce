package ecommerce.productservice.service;


import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.productservice.dto.request.CategoryRequest;
import ecommerce.productservice.dto.response.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    public ApiResponse<CategoryResponse> createCategory(CategoryRequest request);

    public ApiResponse<CategoryResponse> updateCategory(Long id, CategoryRequest request);

    public ApiResponse<List<CategoryResponse>> getAllCategory();

    public ApiResponse<CategoryResponse> deleteCategory(Long id);

}
