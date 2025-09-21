package ecommerce.productservice.repository;

import ecommerce.productservice.dto.response.CategoryResponse;
import ecommerce.productservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);

    Category getAllBy();

    List<Category> findAllByIsActive(Integer isActive);

    @Query("SELECT new ecommerce.productservice.dto.response.CategoryResponse(" +
            "c.id, c.name, c.parent.id, c.isActive,c.image, COUNT(p.id),c.isVisible) " +
            "FROM Category c" +
            " LEFT JOIN Product p ON p.category.id = c.id AND p.isActive=1" +
            "GROUP BY c.id, c.name, c.parent.id, c.isActive, c.image, c.isVisible")
    List<CategoryResponse> findAllCategoryWithProductCount();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id IN :categoryIds AND p.isActive = 1")
    Long countByCategoryIds(@Param("categoryIds") Set<Long> categoryIds);

    List<Category> findByParentId(Long parentId);

    List<Category> findByParentIsNull();

    @Query("""
                SELECT DISTINCT c FROM Category c LEFT JOIN fetch c.children ch WHERE c.parent IS NULL AND c.isActive =1
            """)
    List<Category> findAllParentWithChildren();
}
