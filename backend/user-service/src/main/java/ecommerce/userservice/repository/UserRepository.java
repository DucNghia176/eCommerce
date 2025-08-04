package ecommerce.userservice.repository;

import ecommerce.userservice.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);

    Page<Users> findByIsLock(Integer isLock, Pageable pageable);

    @Query("""
                SELECT
                    COUNT(u),
                    SUM(CASE WHEN u.isLock = 0 AND u.isActive = 1 THEN 1 ELSE 0 END),
                    SUM(CASE WHEN u.isLock = 1 AND u.isActive = 1 THEN 1 ELSE 0 END)
                FROM Users u
                WHERE u.isActive = 1
            """)
    Object countUsersStatus();

}
