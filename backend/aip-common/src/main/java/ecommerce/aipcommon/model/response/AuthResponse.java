package ecommerce.aipcommon.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {
    String username;
    String password;
    String email;
    String token;
    List<String> role;
    String lastLogin;
}
