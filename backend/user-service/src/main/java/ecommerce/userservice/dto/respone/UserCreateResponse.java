package ecommerce.userservice.dto.respone;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateResponse {
    Long id;
    String username;
    String password;
    String email;
    String role;
    Integer isLock;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
