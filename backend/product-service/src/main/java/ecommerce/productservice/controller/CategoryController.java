package ecommerce.productservice.controller;


import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.productservice.dto.request.CategoryRequest;
import ecommerce.productservice.dto.response.CategoryResponse;
import ecommerce.productservice.dto.response.ParentCategoryResponse;
import ecommerce.productservice.dto.response.ProductSummaryResponse;
import ecommerce.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CategoryResponse> createCategory(
            @RequestPart("data") CategoryRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return categoryService.createCategory(request, image);
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategory() {
        return categoryService.getAllCategory();
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestPart CategoryRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return categoryService.updateCategory(id, request, image);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<CategoryResponse> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/{id}/products")
    public ApiResponse<Page<ProductSummaryResponse>> getProductsByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return categoryService.getProductsByCategory(id, page, size);
    }

    @GetMapping("/all-with-featured")
    public ApiResponse<List<ParentCategoryResponse>> getAllWithFeatured() {
        List<ParentCategoryResponse> parentCategoryResponses = categoryService.getAllParentCategories();
        return ApiResponse.<List<ParentCategoryResponse>>builder()
                .code(200)
                .message("Success")
                .data(parentCategoryResponses)
                .build();
    }
}
