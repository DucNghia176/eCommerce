package ecommerce.productservice.spec;

import ecommerce.productservice.dto.request.SearchRequest;
import ecommerce.productservice.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> build(SearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("keyword")), "%" + request.getKeyword().toLowerCase() + "%"));
            }

            if (request.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category"), request.getCategoryId()));
            }

            if (request.getBrandId() != null) {
                predicates.add(cb.equal(root.get("brand"), request.getBrandId()));
            }

            if (request.getPriceFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.getPriceFrom()));
            }

            if (request.getPriceTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), request.getPriceTo()));
            }

            if (request.getRatingFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), request.getRatingFrom()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
