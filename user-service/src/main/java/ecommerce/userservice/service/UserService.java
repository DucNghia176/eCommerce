package ecommerce.userservice.service;

import ecommerce.apicommon.model.response.ApiResponse;
import ecommerce.apicommon.model.response.UserResponse;
import ecommerce.apicommon.model.status.RoleStatus;
import ecommerce.userservice.dto.request.UserRequest;
import ecommerce.userservice.entity.Users;
import ecommerce.userservice.mapper.UserMapper;
import ecommerce.userservice.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse<UserResponse> createUser(UserRequest request) {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                return ApiResponse.<UserResponse>builder()
                        .code(400)
                        .message("Tên đăng nhập đã tồn tại")
                        .data(null)
                        .build();
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                return ApiResponse.<UserResponse>builder()
                        .code(400)
                        .message("Email đã tồn tại")
                        .data(null)
                        .build();
            }
            Users users = userMapper.toEntity(request);

            users.setRole(RoleStatus.USER.toString());
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            users.setPassword(encodedPassword);

            Users save = userRepository.save(users);
            UserResponse response = userMapper.toResponse(save);
            return ApiResponse.<UserResponse>builder()
                    .code(200)
                    .message("Tạo user thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            return ApiResponse.<UserResponse>builder()
                    .code(500)
                    .message("Tạo user không thành công")
                    .data(null)
                    .build();
        }
    }
}
