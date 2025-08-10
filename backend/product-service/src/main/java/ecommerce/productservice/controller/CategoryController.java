package ecommerce.productservice.controller;


import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.productservice.dto.request.CategoryRequest;
import ecommerce.productservice.dto.response.CategoryResponse;
import ecommerce.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CategoryResponse> createCategory(@ModelAttribute CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategory() {
        return categoryService.getAllCategory();
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id, @ModelAttribute CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<CategoryResponse> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}
