package ecommerce.aipcommon.model.response;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String username;
    private String password;
    private String email;
    private String token;
    private String role;
    private String lastLogin;
}
