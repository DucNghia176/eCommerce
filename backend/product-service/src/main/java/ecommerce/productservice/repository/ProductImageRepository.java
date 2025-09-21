package ecommerce.productservice.repository;

import ecommerce.productservice.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);

    void deleteByProductId(Long productId);

    @Query("""
            SELECT pi.imageUrl FROM ProductImage pi WHERE pi.product.id = :productId
            """)
    List<String> findImageUrl(@Param("productId") Long productId);
}
