package ecommerce.userservice.service;

import ecommerce.apicommon.model.response.ApiResponse;
import ecommerce.apicommon.model.response.UserResponse;
import ecommerce.apicommon.model.status.RoleStatus;
import ecommerce.userservice.dto.request.UserRequest;
import ecommerce.userservice.dto.request.UserUpdateRequest;
import ecommerce.userservice.entity.Users;
import ecommerce.userservice.kafka.KafkaUser;
import ecommerce.userservice.mapper.UserMapper;
import ecommerce.userservice.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Getter
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final KafkaUser kafkaUser;

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
            users.setCreatedAt(LocalDateTime.now());
            UserResponse response = userMapper.toResponse(userRepository.save(users));
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

    public ApiResponse<UserResponse> getUserById(Long id) {
        try {
            Users users = userRepository.findById(id)
                    .orElse(null);

            if (users == null) {
                return ApiResponse.<UserResponse>builder()
                        .code(404)
                        .message("Không tìm thấy người dùng với ID: " + id)
                        .data(null)
                        .build();
            }
            UserResponse response = userMapper.toResponse(users);
            return ApiResponse.<UserResponse>builder()
                    .code(200)
                    .message("Lấy thông tin người dùng thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi khi lấy người dùng theo ID: {}", e.getMessage(), e);
            return ApiResponse.<UserResponse>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi khi lấy thông tin người dùng")
                    .data(null)
                    .build();
        }
    }

    public ApiResponse<UserResponse> updateUser(Long id, UserUpdateRequest request) {
        try {
            Users users = userRepository.findById(id)
                    .orElse(null);
            if (users == null) {
                return ApiResponse.<UserResponse>builder()
                        .code(404)
                        .message("Không tìm thấy người dùng với ID: " + id)
                        .data(null)
                        .build();
            }
            users.setFullName(request.getFullName());
            users.setAvatar(request.getAvatar());
            users.setGender(request.getGender());
            users.setDateOfBirth(request.getDateOfBirth());
            users.setUpdatedAt(LocalDateTime.now());
            UserResponse response = userMapper.toResponse(userRepository.save(users));

            return ApiResponse.<UserResponse>builder()
                    .code(200)
                    .message("Cập nhật thành công ")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi: " + e.getMessage(), e);
            kafkaUser.sendMessage("user-events", "Cập nhật thất bại");
            return ApiResponse.<UserResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống: " + id)
                    .data(null)
                    .build();
        }
    }
}