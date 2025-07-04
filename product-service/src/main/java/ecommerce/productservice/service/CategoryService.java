package ecommerce.productservice.service;


import ecommerce.apicommon.dto.response.ApiResponse;
import ecommerce.productservice.dto.request.CategoryRequest;
import ecommerce.productservice.dto.response.CategoryResponse;
import ecommerce.productservice.entity.Category;
import ecommerce.productservice.mapper.CategoryMapper;
import ecommerce.productservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public ApiResponse<CategoryResponse> createCategory(CategoryRequest request) {
        try {
            Category parent = null;

            if (request.getParentId() != null) {
                parent = categoryRepository.findById(request.getParentId())
                        .orElseThrow(() -> new RuntimeException("Parent category not found"));
            }

            Category category = categoryMapper.toEntity(request);
            category.setParent(parent);
            Category saved = categoryRepository.save(category);

            CategoryResponse response = categoryMapper.toResponse(saved);

            return ApiResponse.<CategoryResponse>builder()
                    .code(200)
                    .message("Tạo category thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            return ApiResponse.<CategoryResponse>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi trong hệ thống.")
                    .data(null)
                    .build();

        }
    }

    public ApiResponse<CategoryResponse> updateCategory(Long id, CategoryRequest request) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

            Category parent = null;

            if (request.getParentId() != null) {
                parent = categoryRepository.findById(request.getParentId())
                        .orElseThrow(() -> new RuntimeException("Parent category not found"));
            }

            category.setParent(parent);
            category.setName(request.getName());
            Category update = categoryRepository.save(category);

            CategoryResponse response = categoryMapper.toResponse(update);

            return ApiResponse.<CategoryResponse>builder()
                    .code(200)
                    .message("Cập nhật category thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            return ApiResponse.<CategoryResponse>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi trong hệ thống.")
                    .data(null)
                    .build();
        }
    }

    public ApiResponse<List<CategoryResponse>> getAllCategory() {
        try {
            List<Category> categories = categoryRepository.findAllByIsActive(1);

            List<CategoryResponse> responses = categories.stream()
                    .map(categoryMapper::toResponse)
                    .toList();

            return ApiResponse.<List<CategoryResponse>>builder()
                    .code(200)
                    .message("Lấy danh sách danh mục thành công")
                    .data(responses)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách category: {}", e.getMessage(), e);
            return ApiResponse.<List<CategoryResponse>>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi khi lấy danh sách danh mục.")
                    .data(null)
                    .build();
        }
    }

    public ApiResponse<CategoryResponse> deleteCategory(Long id) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

            category.setIsActive(0);
            categoryRepository.save(category);

            return ApiResponse.<CategoryResponse>builder()
                    .code(200)
                    .message("Xóa category thành công")
                    .data(null)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            return ApiResponse.<CategoryResponse>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi trong hệ thống.")
                    .data(null)
                    .build();
        }
    }

}
