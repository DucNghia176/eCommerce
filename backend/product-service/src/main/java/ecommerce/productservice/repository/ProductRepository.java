package ecommerce.productservice.repository;

import ecommerce.productservice.entity.Product;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findAllByIsActive(Integer isActive);

    @Query("SELECT p.skuCode FROM Product p WHERE p.id = :id")
    String findSkuCodeById(@Param("id") Long id);
}
