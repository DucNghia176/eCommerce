package ecommerce.userservice.repository;

import ecommerce.apicommon1.model.response.UserResponse;
import ecommerce.apicommon1.model.status.GenderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JDBC {
    private final JdbcTemplate jdbcTemplate;

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
                WHERE (? IS NULL OR ui.FULL_NAME LIKE ?)
                  AND (? IS NULL OR ui.GENDER = ?)
                  AND (? IS NULL OR ua.IS_LOCK = ?)
                  AND (? IS NULL OR ua.EMAIL LIKE ?)
                GROUP BY ua.USER_ID, ua.USERNAME, ua.PASSWORD, ui.FULL_NAME, ua.EMAIL,
                         ua.IS_LOCK, ui.AVATAR, ui.GENDER, ui.DATE_OF_BIRTH,
                         ui.ADDRESS, ui.PHONE, ua.CREATE_AT, ua.UPDATE_AT
                ORDER BY ua.USER_ID
                """;

        // Chuẩn bị tham số theo thứ tự trong câu SQL
        List<Object> params = new ArrayList<>();

        // fullName
        params.add(fullName);
        params.add(fullName != null && !fullName.isBlank() ? "%" + fullName + "%" : null);

        // gender
        params.add(gender != null ? gender.name() : null);
        params.add(gender != null ? gender.name() : null);

        // isLock
        params.add(isLock);
        params.add(isLock);

        // email
        params.add(email);
        params.add(email != null && !email.isBlank() ? "%" + email + "%" : null);

        return jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> UserResponse.builder()
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
