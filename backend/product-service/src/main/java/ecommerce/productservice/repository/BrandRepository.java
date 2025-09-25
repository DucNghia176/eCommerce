package ecommerce.productservice.repository;

import ecommerce.productservice.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query("""
                SELECT DISTINCT b
                FROM Brand b
                JOIN Product p ON b.id = p.brandId
                WHERE p.category.id = :categoryId
            """)
    List<Brand> findAllByCategoryId(Long categoryId);
}
