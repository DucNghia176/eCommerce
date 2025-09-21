package ecommerce.productservice.service.impl;


import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.productservice.dto.request.CategoryRequest;
import ecommerce.productservice.dto.response.*;
import ecommerce.productservice.entity.Category;
import ecommerce.productservice.mapper.CategoryMapper;
import ecommerce.productservice.repository.CategoryRepository;
import ecommerce.productservice.repository.ProductRepository;
import ecommerce.productservice.service.CategoryService;
import ecommerce.productservice.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CloudinaryService cloudinaryService;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public ApiResponse<CategoryResponse> createCategory(CategoryRequest request, MultipartFile image) {
        try {
            Category parent = null;

            if (request.getParentId() != null) {
                parent = categoryRepository.findById(request.getParentId())
                        .orElseThrow(() -> new RuntimeException("Parent category not found"));
            }

            Category category = categoryMapper.toEntity(request);


            category.setParent(parent);
            Category saved = categoryRepository.save(category);

            if (image != null && !image.isEmpty()) {
                String url = cloudinaryService.uploadFileCategory(image, category.getId());
                category.setImage(url);
                categoryRepository.save(category);
            }
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

    @Transactional
    @Override
    public ApiResponse<CategoryResponse> updateCategory(Long id, CategoryRequest request, MultipartFile image) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

            Category parent = null;

            if (request.getParentId() != null) {
                parent = categoryRepository.findById(request.getParentId())
                        .orElseThrow(() -> new RuntimeException("Parent category not found"));
            } else {
                category.setParent(null);
            }


            category.setParent(parent);
            category.setName(request.getName());
            category.setIsVisible(request.getIsVisible());


            if (image != null && !image.isEmpty()) {
                String url = cloudinaryService.uploadFileCategory(image, category.getId());
                category.setImage(url);
            }

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

    @Override
    public ApiResponse<List<CategoryResponse>> getAllCategory() {
        try {
            List<CategoryResponse> responses = categoryRepository.findAllCategoryWithProductCount();

            Map<Long, CategoryResponse> map = responses.stream()
                    .collect(Collectors.toMap(CategoryResponse::getId, c -> c));

            for (CategoryResponse c : responses) {
                Long parentId = c.getParentId();
                if (parentId != null) {
                    CategoryResponse parent = map.get(parentId);
                    if (parent != null) {
                        parent.setProductCount(parent.getProductCount() + c.getProductCount());
                    }
                }
            }

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

    @Override
    public ApiResponse<CategoryResponse> deleteCategory(Long id) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

            category.setIsActive(0);
            categoryRepository.save(category);

            productRepository.deactivateProductsByCategory(id);

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

    @Override
    public ApiResponse<CategoryResponse> getCategoryById(Long id) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy category với ID: " + id));

            CategoryResponse response = CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .parentId(category.getParent() != null ? category.getParent().getId() : null)
                    .image(category.getImage())
                    .isVisible(category.getIsVisible())
                    .build();
            return ApiResponse.<CategoryResponse>builder()
                    .code(200)
                    .message("")
                    .data(response)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<CategoryResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Page<ProductSummaryResponse>> getProductsByCategory(Long id, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

            Set<Long> categoryIds = getAllCategoryIds(id);
            categoryIds.add(id);
            Page<ProductSummaryResponse> products = productRepository.findProductsByCategory(categoryIds, pageable);

            return ApiResponse.<Page<ProductSummaryResponse>>builder()
                    .code(200)
                    .message("Lấy products theo category thành công")
                    .data(products)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Page<ProductSummaryResponse>>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }

    public Set<Long> getAllCategoryIds(Long categoryId) {
        Set<Long> ids = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();
        queue.add(categoryId);

        while (!queue.isEmpty()) {
            Long currentId = queue.poll();
            ids.add(currentId);
            List<Category> children = categoryRepository.findByParentId((currentId));
            for (Category child : children) {
                queue.add(child.getId());
            }
        }
        return ids;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParentCategoryResponse> getAllParentCategories() {
        List<Category> parents = categoryRepository.findAllParentWithChildren();

        return parents.stream()
                .map(parent -> {
                    List<ChildCategoryResponse> childCategory = parent.getChildren().stream()
                            .map(child -> {
                                List<FeaturedProductResponse> featuredProduct = productRepository.findTopByCategoryIdAndTagName(child.getId(), "FEATURE", PageRequest.of(0, 3));
                                return new ChildCategoryResponse(child.getId(), child.getName(), featuredProduct);
                            })
                            .toList();
                    return new ParentCategoryResponse(parent.getId(), parent.getName(), childCategory);
                })
                .toList();
    }
}
