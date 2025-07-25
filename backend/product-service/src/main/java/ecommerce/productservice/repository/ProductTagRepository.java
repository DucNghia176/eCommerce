package ecommerce.productservice.repository;

import ecommerce.productservice.entity.ProductTag;
import ecommerce.productservice.entity.imp.ProductTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTag, ProductTagId> {
    List<ProductTag> findByProduct_Id(Long productId);
}
