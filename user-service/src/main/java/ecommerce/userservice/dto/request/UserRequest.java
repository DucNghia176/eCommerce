package ecommerce.userservice.dto.request;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String role;
    private String address;
    private String city;
    private String country;
}
