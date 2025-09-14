package ecommerce.userservice.repository;

import ecommerce.aipcommon.model.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JDBCNamed {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<UserResponse> searchUsers(String fullName, String gender, Integer isLock, String email) {
        String sql = """
                SELECT ua.id, ua.username, ua.password, ui.full_name, ua.email,
                       LISTAGG(r.role_name, ',') WITHIN GROUP (ORDER BY r.role_name) AS roles,
                       ua.is_lock, ui.avatar, ui.gender, ui.date_of_birth,
                       ui.address, ui.phone, ua.created_at, ua.updated_at
                FROM user_acc ua
                JOIN user_info ui ON ua.id = ui.user_id
                JOIN user_role ur ON ua.id = ur.user_id
                JOIN role r ON ur.role_id = r.id
                WHERE (:fullName IS NULL OR ui.full_name LIKE :fullNameLike)
                  AND (:gender IS NULL OR ui.gender = :gender)
                  AND (:isLock IS NULL OR ua.is_lock = :isLock)
                  AND (:email IS NULL OR ua.email LIKE :emailLike)
                GROUP BY ua.id, ua.username, ua.password, ui.full_name, ua.email,
                         ua.is_lock, ui.avatar, ui.gender, ui.date_of_birth,
                         ui.address, ui.phone, ua.created_at, ua.updated_at
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("fullName", fullName);
        params.put("fullNameLike", fullName != null ? "%" + fullName + "%" : null);
        params.put("gender", gender);
        params.put("isLock", isLock);
        params.put("email", email);
        params.put("emailLike", email != null ? "%" + email + "%" : null);

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> UserResponse.builder()
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
                .build());
    }
}
