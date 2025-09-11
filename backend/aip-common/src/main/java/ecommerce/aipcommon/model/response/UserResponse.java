package ecommerce.aipcommon.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String username;
    String password;
    String fullName;
    String email;
    String role;
    Integer isLock;
    String avatar;
    String gender;
    LocalDate dateOfBirth;
    String address;
    String phone;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;


}
