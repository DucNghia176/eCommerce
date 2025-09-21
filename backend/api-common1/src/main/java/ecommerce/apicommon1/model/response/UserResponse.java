package ecommerce.apicommon1.model.response;

import ecommerce.apicommon1.model.status.GenderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    String roles;
    Integer isLock;
    String avatar;
    String gender;
    LocalDate dateOfBirth;
    String address;
    String phone;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public UserResponse(Long id, String username, String password, String fullName, String email, Integer isLock, String avatar, GenderStatus gender, LocalDate dateOfBirth, String address, String phone, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.isLock = isLock;
        this.avatar = avatar;
        this.gender = gender == null ? null : gender.name();
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
