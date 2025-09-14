package ecommerce.userservice.repository;

import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.aipcommon.model.status.GenderStatus;
import ecommerce.userservice.entity.UserAcc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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


    @Query(value = """
            SELECT ua.id, ua.username, ua.password, ui.fullName, ua.email,
                        LISTAGG(r.roleName, ',') WITHIN GROUP (ORDER BY r.roleName) AS ROLES,
                   ua.isLock, ui.avatar, ui.gender, ui.dateOfBirth,
                   ui.address, ui.phone, ua.createdAt, ua.updatedAt
            FROM UserAcc ua
            JOIN UserInfo ui ON ua.id = ui.id
            left JOIN ua.roles r
            WHERE (:fullName IS NULL OR ui.fullName LIKE %:fullName%)
              AND (:gender IS NULL OR ui.gender = :gender)
              AND (:isLock IS NULL OR ua.isLock = :isLock)
              AND (:email IS NULL OR ua.email LIKE %:email%)
            GROUP BY ua.id, ua.username, ua.password, ui.fullName, ua.email,
                     ua.isLock, ui.avatar, ui.gender, ui.dateOfBirth,
                     ui.address, ui.phone, ua.createdAt, ua.updatedAt
            """)
    List<UserResponse> searchUsers(
            @Param("fullName") String fullName,
            @Param("gender") GenderStatus gender,
            @Param("isLock") Integer isLock,
            @Param("email") String email
    );
}
