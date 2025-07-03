package ecommerce.productservice.service;


import ecommerce.productservice.dto.request.CategoryRequest;
import ecommerce.productservice.dto.response.CategoryResponse;
import ecommerce.productservice.entity.Category;
import ecommerce.productservice.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        try {
            Category parent = null;

            if (request.getParentId() != null) {
                parent = categoryRepository.findById(request.getParentId())
                        .orElseThrow(() -> new RuntimeException("Parent category not found"));
            }

            Category category = Category.builder()
                    .name(request.getName())
                    .isActive(1)
                    .build();

            Category saved = categoryRepository.save(category);

            return CategoryResponse.builder()
                    .id(saved.getId())
                    .name(saved.getName())
                    .parentId(parent != null ? parent.getId() : null)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tạo category: " + e.getMessage());
        }
    }
}
