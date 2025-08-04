package ecommerce.userservice.service.impl;

import ecommerce.aipcommon.config.TokenInfo;
import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.aipcommon.model.status.RoleStatus;
import ecommerce.userservice.dto.request.UserUpdateRequest;
import ecommerce.userservice.dto.respone.CountResponse;
import ecommerce.userservice.entity.Users;
import ecommerce.userservice.kafka.KafkaUser;
import ecommerce.userservice.mapper.UserMapper;
import ecommerce.userservice.repository.UserRepository;
import ecommerce.userservice.service.CloudinaryService;
import ecommerce.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaUser kafkaUser;
    private final CloudinaryService cloudinaryService;
    private final TokenInfo tokenInfo;

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

    @Override
    public ApiResponse<Page<UserResponse>> getAllUsers(int page, int size, Integer isLock) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Users> usersPage;

            // Nếu có lọc theo isLock
            if (isLock == null) {
                usersPage = userRepository.findAll(pageable);
            } else {
                usersPage = userRepository.findByIsLock(isLock, pageable);
            }

            Page<UserResponse> responses = usersPage.map(userMapper::toResponse);

            return ApiResponse.<Page<UserResponse>>builder()
                    .code(200)
                    .message("Lấy danh sách người dùng thành công")
                    .data(responses)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách người dùng: {}", e.getMessage(), e);
            return ApiResponse.<Page<UserResponse>>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi khi lấy danh sách người dùng")
                    .data(null)
                    .build();
        }
    }


    public ApiResponse<CountResponse> count() {
        Object[] result = (Object[]) userRepository.countUsersStatus();
        Long all = ((Number) result[0]).longValue();
        Long active = ((Number) result[1]).longValue();
        Long inactive = ((Number) result[2]).longValue();

        CountResponse count = CountResponse.builder()
                .all(all)
                .active(active)
                .inactive(inactive)
                .build();

        return ApiResponse.<CountResponse>builder()
                .code(200)
                .message("Lấy dữ liệu thành công")
                .data(count)
                .build();
    }

}