package ecommerce.userservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.aipcommon.model.status.RoleStatus;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final CloudinaryService cloudinaryService;

    public ApiResponse<UserResponse> createUser(UserRequest request, MultipartFile avatarFile) {
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
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarUrl = cloudinaryService.uploadFile(avatarFile);
                users.setAvatar(avatarUrl);
            }
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            users.setPassword(encodedPassword);
            users.setCreatedAt(LocalDateTime.now());
            try {
                users.setGender(request.getGender());
            } catch (IllegalArgumentException e) {
                return ApiResponse.<UserResponse>builder()
                        .code(400)
                        .message("Giới tính không hợp lệ. Chỉ được phép 'Nam' hoặc 'Nữ'")
                        .data(null)
                        .build();
            }
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

    public ApiResponse<UserResponse> updateUser(Long id, UserUpdateRequest request, MultipartFile avatarFile) {
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
            users.setFullName(request.getFullName().trim());
            // Xử lý ảnh đại diện
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String oldAvatarUrl = users.getAvatar();
                if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
                    String publicId = cloudinaryService.extractPublicId(oldAvatarUrl);
                    if (publicId != null) {
                        cloudinaryService.deleteFile(publicId);
                    }
                }
                // Upload ảnh mới
                String avatarUrl = cloudinaryService.uploadFile(avatarFile);
                users.setAvatar(avatarUrl);
            }

            try {
                users.setGender(request.getGender());
            } catch (IllegalArgumentException e) {
                return ApiResponse.<UserResponse>builder()
                        .code(400)
                        .message("Giới tính không hợp lệ. Chỉ được phép 'Nam' hoặc 'Nữ'")
                        .data(null)
                        .build();
            }
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