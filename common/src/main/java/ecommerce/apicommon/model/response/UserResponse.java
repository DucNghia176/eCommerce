package ecommerce.apicommon.model.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;
    private Integer isActive;
    private String avatar;
    private LocalDateTime createdAt;
    private String gender;
    private LocalDate dateOfBirth;
}
