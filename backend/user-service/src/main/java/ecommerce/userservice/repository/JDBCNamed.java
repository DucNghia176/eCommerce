package ecommerce.userservice.repository;

import ecommerce.apicommon1.model.response.UserResponse;
import ecommerce.apicommon1.model.status.GenderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JDBCNamed {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<UserResponse> searchUsers(String fullName, GenderStatus gender, Integer isLock, String email) {
        String sql = """
                SELECT ua.USER_ID,
                       ua.USERNAME,
                       ua.PASSWORD,
                       ui.FULL_NAME,
                       ua.EMAIL,
                       ua.IS_LOCK,
                       ui.AVATAR,
                       ui.GENDER,
                       ui.DATE_OF_BIRTH,
                       ui.ADDRESS,
                       ui.PHONE,
                       ua.CREATE_AT,
                       ua.UPDATE_AT,
                       LISTAGG(r.ROLE_NAME, ',') WITHIN GROUP (ORDER BY r.ROLE_NAME) AS ROLES
                FROM USER_ACCOUNT ua
                JOIN USER_PROFILE ui ON ua.USER_ID = ui.USER_ID
                LEFT JOIN USER_ROLE ur ON ua.USER_ID = ur.USER_ID
                LEFT JOIN ROLE r ON ur.ROLE_ID = r.ROLE_ID
                WHERE (:fullName IS NULL OR ui.FULL_NAME LIKE '%' || :fullName || '%')
                  AND (:gender IS NULL OR ui.GENDER = :gender)
                  AND (:isLock IS NULL OR ua.IS_LOCK = :isLock)
                  AND (:email IS NULL OR ua.EMAIL LIKE '%' || :email || '%')
                GROUP BY ua.USER_ID, ua.USERNAME, ua.PASSWORD, ui.FULL_NAME, ua.EMAIL,
                         ua.IS_LOCK, ui.AVATAR, ui.GENDER, ui.DATE_OF_BIRTH,
                         ui.ADDRESS, ui.PHONE, ua.CREATE_AT, ua.UPDATE_AT
                ORDER BY ua.USER_ID
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("fullName", (fullName != null && !fullName.isBlank()) ? fullName : null);
        params.put("gender", gender != null ? gender.name() : null);
        params.put("isLock", isLock);
        params.put("email", (email != null && !email.isBlank()) ? email : null);

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> UserResponse.builder()
                .id(rs.getLong("USER_ID"))
                .username(rs.getString("USERNAME"))
                .password(rs.getString("PASSWORD"))
                .fullName(rs.getString("FULL_NAME"))
                .email(rs.getString("EMAIL"))
                .isLock(rs.getInt("IS_LOCK"))
                .avatar(rs.getString("AVATAR"))
                .gender(rs.getString("GENDER"))
                .dateOfBirth(rs.getDate("DATE_OF_BIRTH") != null ? rs.getDate("DATE_OF_BIRTH").toLocalDate() : null)
                .address(rs.getString("ADDRESS"))
                .phone(rs.getString("PHONE"))
                .createdAt(rs.getTimestamp("CREATE_AT").toLocalDateTime())
                .updatedAt(rs.getTimestamp("UPDATE_AT").toLocalDateTime())
                .roles(rs.getString("ROLES"))
                .build());
    }
}
