package ecommerce.userservice.repository;

import ecommerce.apicommon1.model.status.GenderStatus;
import ecommerce.userservice.entity.UserAcc;
import ecommerce.userservice.entity.UserInfo;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<UserAcc> searchUsers(String fullName, GenderStatus gender, Integer isLock, String email) {
        return (Root<UserAcc> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            // join UserInfo
            Join<UserAcc, UserInfo> userInfoJoin = root.join("userInfo", JoinType.INNER);
            // fetch roles
            root.fetch("roles", JoinType.LEFT);

            query.distinct(true);

            Predicate p = cb.conjunction();

            if (fullName != null) {
                p = cb.and(p, cb.like(userInfoJoin.get("fullName"), "%" + fullName + "%"));
            }
            if (gender != null) {
                p = cb.and(p, cb.equal(userInfoJoin.get("gender"), gender));
            }
            if (isLock != null) {
                p = cb.and(p, cb.equal(root.get("isLock"), isLock));
            }
            if (email != null) {
                p = cb.and(p, cb.like(root.get("email"), "%" + email + "%"));
            }

            return p;
        };
    }
}
