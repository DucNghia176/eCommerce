package ecommerce.productservice.service;


import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.productservice.dto.request.CategoryRequest;
import ecommerce.productservice.dto.response.CategoryResponse;
import ecommerce.productservice.dto.response.ChildCategoryResponse;
import ecommerce.productservice.dto.response.ProductSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface CategoryService {

    ApiResponse<CategoryResponse> createCategory(CategoryRequest request, MultipartFile image);

    ApiResponse<CategoryResponse> updateCategory(Long id, CategoryRequest request, MultipartFile image);

    ApiResponse<List<CategoryResponse>> getAllCategory();

    ApiResponse<CategoryResponse> deleteCategory(Long id);

    ApiResponse<CategoryResponse> getCategoryById(Long id);

    ApiResponse<Page<ProductSummaryResponse>> getProductsByCategory(Long id, int page, int size);

    List<ChildCategoryResponse> getAllChildCategoriesWithBrandAndProduct();
}
