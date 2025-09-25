package ecommerce.productservice.repository;

import ecommerce.productservice.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {
    Optional<AttributeValue> findByValue(String value);

    Optional<AttributeValue> findByValueIgnoreCase(String value);
}
