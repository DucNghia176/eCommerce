package ecommerce.productservice.repository;

import ecommerce.productservice.dto.response.*;
import ecommerce.productservice.entity.Product;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Modifying
    @Query("UPDATE Product p SET p.isActive = 0 WHERE p.category.id = :categoryId")
    void deactivateProductsByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT p.skuCode FROM Product p WHERE p.id = :id")
    String findSkuCodeById(@Param("id") Long id);


    @Query("SELECT new ecommerce.productservice.dto.response.ProductResponse(" +
            "p.id ,p.name, p.price, c.name, p.description, new ecommerce.productservice.dto.response.BrandResponse(b.id, b.name), pi.imageUrl, p.skuCode)" +
            " FROM Product p " +
            "left JOIN Brand b ON b.id = p.brandId " +
            "left JOIN Category c ON c.id = p.category.id " +
            "left JOIN ProductImage pi ON pi.product.id = p.id AND pi.isThumbnail = 1" +
            "WHERE p.isActive = 1")
    Page<ProductResponse> findAllActiveProducts(Pageable pageable);

    @Query("SELECT new ecommerce.productservice.dto.response.ProductSummaryResponse(p.id, p.name, pi.imageUrl) " +
            "FROM Product p " +
            "LEFT JOIN ProductImage pi ON pi.product.id = p.id " +
            "WHERE p.category.id IN :categoryIds " +
            "AND p.category.isActive = 1 AND p.isActive = 1")
    Page<ProductSummaryResponse> findProductsByCategory(@Param("categoryIds") Set<Long> categoryIds, Pageable pageable);

    @Query("""
            SELECT new ecommerce.productservice.dto.response.FeaturedProductResponse(p.id, p.name, p.price, pi.imageUrl) FROM Product p
                        JOIN p.tags t
                        LEFT JOIN ProductImage pi ON pi.product.id = p.id AND pi.isThumbnail = 1
                        WHERE p.category.id = :categoryId
                        AND t.name = :tagName
                        AND p.isActive = 1
                        ORDER BY p.id DESC
            """)
    List<FeaturedProductResponse> findTopByCategoryIdAndTagName(@Param("categoryId") Long categoryId, @Param("tagName") String tagName, Pageable pageable);

    @Query("""
            SELECT new ecommerce.productservice.dto.response.ProductViewResponse(p.id,p.name,p.skuCode,p.price, b.name, p.category.name, p.averageRating, p.totalReviews)
                         FROM Product p
                         LEFT JOIN Brand b ON b.id = p.brandId
                         WHERE p.id = :productId
            """)
    ProductViewResponse findProduct(@Param("productId") Long productId);

    @Query("""
                SELECT new ecommerce.productservice.dto.response.SearchProductResponse(
                    p.id, p.name, pi.imageUrl, p.price, p.discount, p.averageRating, p.totalReviews
                )
                FROM Product p
                LEFT JOIN ProductImage pi ON pi.product.id = p.id AND pi.isThumbnail = 1
                WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
                  AND (:categoryId IS NULL OR p.category.id = :categoryId)
                  AND (:brandId IS NULL OR p.brandId = :brandId)
                  AND (:priceFrom IS NULL OR p.price >= :priceFrom)
                  AND (:priceTo IS NULL OR p.price <= :priceTo)
                  AND (:ratingFrom IS NULL OR p.averageRating >= :ratingFrom)
            """)
    Page<SearchProductResponse> searchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            @Param("priceFrom") BigDecimal priceFrom,
            @Param("priceTo") BigDecimal priceTo,
            @Param("ratingFrom") Double ratingFrom,
            Pageable pageable
    );

    @Query("""
                SELECT DISTINCT p
                FROM Product p
                JOIN FETCH p.category
                JOIN p.tags t
                WHERE t.id IN :tagIds
            """)
    Page<Product> getAllByTagIds(@Param("tagIds") List<Long> tagIds, Pageable pageable);

    @Query("SELECT p.id FROM Product p WHERE p.category.id = :categoryId AND p.id <> :productId")
    List<Long> findRelatedProductIds(@Param("categoryId") Long categoryId, @Param("productId") Long productId);
}