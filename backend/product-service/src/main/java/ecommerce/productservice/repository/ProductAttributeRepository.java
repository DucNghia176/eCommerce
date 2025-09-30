package ecommerce.productservice.repository;

import ecommerce.productservice.dto.response.ProductAttributeProjection;
import ecommerce.productservice.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    @Query("""
            SELECT pa.id AS id,
                   att.name AS attribute,
                   v.value AS value
            FROM ProductAttribute pa
                     JOIN pa.attribute att
                     JOIN pa.value v
            WHERE pa.product.id = :productId
            """)
    List<ProductAttributeProjection> findAttributesByProductId(@Param("productId") Long productId);

    void deleteByProductId(Long productId);
}
