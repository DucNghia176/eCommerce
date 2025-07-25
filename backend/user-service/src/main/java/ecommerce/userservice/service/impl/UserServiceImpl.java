package ecommerce.userservice.service.impl;

import ecommerce.aipcommon.config.TokenInfo;
import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.aipcommon.model.status.RoleStatus;
import ecommerce.aipcommon.util.JwtUtil;
import ecommerce.userservice.dto.request.UserRequest;
import ecommerce.userservice.dto.request.UserUpdateRequest;
import ecommerce.userservice.entity.Users;
import ecommerce.userservice.kafka.KafkaUser;
import ecommerce.userservice.mapper.UserMapper;
import ecommerce.userservice.repository.UserRepository;
import ecommerce.userservice.service.CloudinaryService;
import ecommerce.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final KafkaUser kafkaUser;
    private final CloudinaryService cloudinaryService;
    private final JwtUtil jwtUtil;
    private final TokenInfo tokenInfo;

    @Override
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

    @Override
    public ApiResponse<UserResponse> getUserById() {
        try {
            log.info("Get request by userId={}, role={}", tokenInfo.getUserId(), tokenInfo.getRole());
            Long id = tokenInfo.getUserId();
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

    @Override
    public ApiResponse<UserResponse> updateUser(UserUpdateRequest request, MultipartFile avatarFile) {
        try {
            Long id = tokenInfo.getUserId();
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
                    .message("Lỗi hệ thống:")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<UserResponse> toggleUserLock(Long id) {
        try {
            Users user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return ApiResponse.<UserResponse>builder()
                        .code(404)
                        .message("Không tìm thấy người dùng với ID: " + id)
                        .data(null)
                        .build();
            }

            Integer currentStatus = user.getIsLock() == null ? 0 : user.getIsLock();
            Integer newStatus = currentStatus == 1 ? 0 : 1;

            user.setIsLock(newStatus);
            user.setUpdatedAt(LocalDateTime.now());

            UserResponse response = userMapper.toResponse(userRepository.save(user));

            return ApiResponse.<UserResponse>builder()
                    .code(200)
                    .message(newStatus == 1 ? "Tài khoản đã bị khóa" : "Tài khoản đã được mở khóa")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi khi đảo trạng thái khóa: {}", e.getMessage(), e);
            return ApiResponse.<UserResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<UserResponse> toggleUserRole(Long id) {
        try {
            Users user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return ApiResponse.<UserResponse>builder()
                        .code(404)
                        .message("Không tìm thấy người dùng với ID: " + id)
                        .data(null)
                        .build();
            }

            String currentRole = user.getRole();
            String newRole = currentRole.equals(RoleStatus.ADMIN.toString())
                    ? RoleStatus.USER.toString()
                    : RoleStatus.ADMIN.toString();

            user.setRole(newRole);
            user.setUpdatedAt(LocalDateTime.now());

            UserResponse response = userMapper.toResponse(userRepository.save(user));
            return ApiResponse.<UserResponse>builder()
                    .code(200)
                    .message("Cập nhật quyền thành: " + newRole)
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi khi chuyển quyền: {}", e.getMessage(), e);
            return ApiResponse.<UserResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }
}