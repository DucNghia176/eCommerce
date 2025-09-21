package ecommerce.userservice.repository;

import ecommerce.apicommon1.model.status.GenderStatus;
import ecommerce.userservice.entity.UserAcc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccRepository extends JpaRepository<UserAcc, Long>, JpaSpecificationExecutor<UserAcc> {
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


    @Query("""
                SELECT ua, r
                FROM UserAcc ua
                LEFT JOIN ua.userInfo ui
                LEFT JOIN ua.roles r
                WHERE ua.isActive = 1
                  AND (:fullName IS NULL OR ui.fullName LIKE CONCAT('%', :fullName, '%'))
                  AND (:gender IS NULL OR ui.gender = :gender)
                  AND (:isLock IS NULL OR ua.isLock = :isLock)
                  AND (:email IS NULL OR ua.email LIKE CONCAT('%', :email, '%'))
            """)
    List<UserAcc> searchUsers(
            @Param("fullName") String fullName,
            @Param("gender") GenderStatus gender,
            @Param("isLock") Integer isLock,
            @Param("email") String email
    );
    
    Optional<UserAcc> findByEmail(String email);

    Optional<UserAcc> findByGoogleId(String googleId);

    Optional<UserAcc> findByFacebookId(String facebookId);
}
