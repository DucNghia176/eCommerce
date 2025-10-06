package ecommerce.productservice.repository;


import ecommerce.productservice.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByNameIn(Collection<String> names);
}
