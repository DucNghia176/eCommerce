package ecommerce.userservice.repository;

import ecommerce.apicommon1.model.response.UserResponse;
import ecommerce.apicommon1.model.status.GenderStatus;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.OracleTypes;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class StoreRepo {
    private final DataSource dataSource;

    public List<UserResponse> searchUsers(String fullName, GenderStatus gender, Integer isLock, String email) throws SQLException {
        List<UserResponse> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{call SP_SEARCH_USERS(?, ?, ?, ?, ?)}")) {

            stmt.setString(1, fullName);
            stmt.setString(2, gender.name());
            if (isLock != null) stmt.setInt(3, isLock);
            else stmt.setNull(3, Types.INTEGER);
            stmt.setString(4, email);
            stmt.registerOutParameter(5, OracleTypes.CURSOR);

            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(5)) {
                while (rs.next()) {
                    UserResponse user = new UserResponse(
                            rs.getLong("USER_ID"),
                            rs.getString("USERNAME"),
                            rs.getString("PASSWORD"),
                            rs.getString("FULL_NAME"),
                            rs.getString("EMAIL"),
                            rs.getString("ROLES"),
                            rs.getInt("IS_LOCK"),
                            rs.getString("AVATAR"),
                            rs.getString("GENDER"),
                            rs.getDate("DATE_OF_BIRTH") != null ? rs.getDate("DATE_OF_BIRTH").toLocalDate() : null,
                            rs.getString("ADDRESS"),
                            rs.getString("PHONE"),
                            rs.getTimestamp("CREATE_AT") != null ? rs.getTimestamp("CREATE_AT").toLocalDateTime() : null,
                            rs.getTimestamp("UPDATE_AT") != null ? rs.getTimestamp("UPDATE_AT").toLocalDateTime() : null
                    );
                    result.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // hoặc log
            // bạn có thể ném lại RuntimeException nếu muốn
            throw new RuntimeException("Lỗi khi gọi SP_SEARCH_USERS", e);
        }

        return result;
    }
}
