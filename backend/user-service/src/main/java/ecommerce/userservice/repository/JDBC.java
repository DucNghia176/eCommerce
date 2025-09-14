package ecommerce.userservice.repository;

import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.aipcommon.model.status.GenderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JDBC {
    private final JdbcTemplate jdbcTemplate;

    public List<UserResponse> searchUsers(String fullName, GenderStatus gender, Integer isLock, String email) {
        String sql = """
                    SELECT ua.USER_ID, ua.USERNAME, ua.PASSWORD, ui.FULL_NAME, ua.EMAIL,
                           LISTAGG(r.ROLE_NAME, ',') WITHIN GROUP (ORDER BY r.ROLE_NAME) AS ROLES,
                           ua.IS_LOCK, ui.AVATAR, ui.GENDER, ui.DATE_OF_BIRTH,
                           ui.ADDRESS, ui.PHONE, ua.CREATE_AT, ua.UPDATE_AT
                    FROM USER_ACCOUNT ua
                    JOIN USER_PROFILE ui ON ua.USER_ID = ui.USER_ID
                    JOIN USER_ROLE ur ON ua.USER_ID = ur.USER_ID
                    JOIN ROLE r ON ur.ROLE_ID = r.ID
                    WHERE (:fullName IS NULL OR ui.FULL_NAME LIKE :fullNameLike)
                      AND (:gender IS NULL OR ui.GENDER = :gender)
                      AND (:isLock IS NULL OR ua.IS_LOCK = :isLock)
                      AND (:email IS NULL OR ua.EMAIL LIKE :emailLike)
                    GROUP BY ua.USER_ID, ua.USERNAME, ua.PASSWORD, ui.FULL_NAME, ua.EMAIL,
                             ua.IS_LOCK, ui.AVATAR, ui.GENDER, ui.DATE_OF_BIRTH,
                             ui.ADDRESS, ui.PHONE, ua.CREATE_AT, ua.UPDATE_AT
                """;

        return jdbcTemplate.query(sql, new Object[]{
                        fullName, "%" + fullName + "%",
                        gender, gender,
                        isLock, isLock,
                        email, "%" + email + "%"
                },
                (rs, rowNum) -> UserResponse.builder()
                        .id(rs.getLong("id"))
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .fullName(rs.getString("full_name"))
                        .email(rs.getString("email"))
                        .roles(Arrays.asList(rs.getString("roles").split(",")))
                        .isLock(rs.getInt("is_lock"))
                        .avatar(rs.getString("avatar"))
                        .gender(rs.getString("gender"))
                        .dateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null)
                        .address(rs.getString("address"))
                        .phone(rs.getString("phone"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                        .build()
        );
    }
}
