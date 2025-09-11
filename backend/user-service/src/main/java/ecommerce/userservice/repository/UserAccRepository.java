package ecommerce.userservice.repository;

import ecommerce.userservice.entity.UserAcc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccRepository extends JpaRepository<UserAcc, Long> {
    Optional<UserAcc> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    Page<UserAcc> findByIsLock(Integer isLock, Pageable pageable);

    boolean existsByEmail(String email);

    @Query("""
                SELECT
                    COUNT(u),
                    SUM(CASE WHEN u.isLock = 0 AND u.isActive = 1 THEN 1 ELSE 0 END),
                    SUM(CASE WHEN u.isLock = 1 AND u.isActive = 1 THEN 1 ELSE 0 END)
                FROM UserAcc u
                WHERE u.isActive = 1
            """)
    Object countUsersStatus();
}
