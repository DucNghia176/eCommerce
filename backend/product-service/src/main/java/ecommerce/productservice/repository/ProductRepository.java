package ecommerce.productservice.repository;

import ecommerce.productservice.dto.response.ProductSummaryResponse;
import ecommerce.productservice.entity.Product;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findAllByIsActive(Integer isActive);

    @Modifying
    @Query("UPDATE Product p SET p.isActive = 0 WHERE p.category.id = :categoryId")
    void deactivateProductsByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT p.skuCode FROM Product p WHERE p.id = :id")
    String findSkuCodeById(@Param("id") Long id);

    Page<Product> findAllByIsActive(int isActive, Pageable pageable);

    @Query("SELECT new ecommerce.productservice.dto.response.ProductSummaryResponse(p.id, p.name, pi.imageUrl) " +
            "FROM Product p " +
            "LEFT JOIN ProductImage pi ON pi.product.id = p.id " +
            "WHERE p.category.id IN :categoryIds " +
            "AND pi.isThumbnail = 1 AND p.category.isActive = 1 AND p.isActive = 1")
    Page<ProductSummaryResponse> findProductsByCategory(@Param("categoryIds") Set<Long> categoryIds, Pageable pageable);


    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    Long countByCategoryId(@Param("categoryId") Long categoryId);

}
