package ecommerce.productservice.repository;

import ecommerce.productservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);

    Category getAllBy();

    List<Category> findAllByIsActive(Integer isActive);

}
