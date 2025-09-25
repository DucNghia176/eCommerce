package ecommerce.productservice.repository;

import ecommerce.productservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.productId = :productId")
    Double getAverageScoreByProductId(@Param("productId") Long productId);

    Long countByProductId(Long productId);
}
