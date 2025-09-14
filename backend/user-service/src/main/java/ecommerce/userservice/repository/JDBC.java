package ecommerce.userservice.repository;

import ecommerce.aipcommon.model.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JDBC {
    private final JdbcTemplate jdbcTemplate;

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
                WHERE (? IS NULL OR ui.full_name LIKE ?)
                  AND (? IS NULL OR ui.gender = ?)
                  AND (? IS NULL OR ua.is_lock = ?)
                  AND (? IS NULL OR ua.email LIKE ?)
                GROUP BY ua.id, ua.username, ua.password, ui.full_name, ua.email,
                         ua.is_lock, ui.avatar, ui.gender, ui.date_of_birth,
                         ui.address, ui.phone, ua.created_at, ua.updated_at
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
