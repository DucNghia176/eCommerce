package ecommerce.userservice.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PendingRegistration {
    private UserRequest userRequest;
    private MultipartFile avatar;
}

