package ecommerce.userservice.service.impl;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.AuthResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.aipcommon.model.status.RoleStatus;
import ecommerce.aipcommon.util.JwtUtil;
import ecommerce.userservice.dto.request.AuthRequest;
import ecommerce.userservice.dto.request.PendingRegistration;
import ecommerce.userservice.dto.request.UserRequest;
import ecommerce.userservice.email.PendingRegistrationStorage;
import ecommerce.userservice.entity.Users;
import ecommerce.userservice.mapper.UserMapper;
import ecommerce.userservice.repository.UserRepository;
import ecommerce.userservice.service.AuthService;
import ecommerce.userservice.service.CloudinaryService;
import ecommerce.userservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;
    private final PendingRegistrationStorage pendingRegistrationStorage;
    @Value("${jwt.signing-key}")
    private String SIGNER_KEY;

    @Override
    public ApiResponse<AuthResponse> login(AuthRequest request) {
        try {
            Optional<Users> usersOpt = userRepository.findByUsername(request.getUsernameOrEmail());
            if (usersOpt.isEmpty()) {
                usersOpt = userRepository.findByEmail(request.getUsernameOrEmail());
            }
            if (usersOpt.isEmpty()) {
                return ApiResponse.<AuthResponse>builder()
                        .code(404)
                        .message("Tài khoản không tồn tại!")
                        .data(null)
                        .build();
            }
            Users user = usersOpt.get();
            if (user.getIsLock() != null && user.getIsLock() == 1) {
                return ApiResponse.<AuthResponse>builder()
                        .code(403)
                        .message("Tài khoản đã bị khóa!")
                        .data(null)
                        .build();
            }
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ApiResponse.<AuthResponse>builder()
                        .code(401)
                        .message("Sai mật khẩu!")
                        .data(null)
                        .build();
            }
            Map<String, Object> claims = Map.of(
                    "userId", user.getId(),
                    "role", user.getRole());
            String subject = user.getUsername() + "/" + user.getEmail();
            String token = jwtUtil.generateToken(claims, subject, SIGNER_KEY);

            AuthResponse response = new AuthResponse();
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setToken(token);
            response.setLastLogin(String.valueOf(LocalDateTime.now()));

            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            return ApiResponse.<AuthResponse>builder()
                    .code(200)
                    .message("Đăng nhập thành công")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            return ApiResponse.<AuthResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }

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

            // Gửi OTP
            ApiResponse<String> otpResponse = emailService.sendOtp(request.getEmail());
            if (otpResponse.getCode() != 200) {
                return ApiResponse.<UserResponse>builder()
                        .code(500)
                        .message("Không thể gửi OTP")
                        .build();
            }

            pendingRegistrationStorage.store(
                    request.getEmail(),
                    PendingRegistration.builder()
                            .userRequest(request)
                            .avatar(avatarFile)
                            .build()
            );

            UserResponse response = UserResponse.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .gender(request.getGender().toString())
                    .dateOfBirth(request.getDateOfBirth())
                    .build();

            return ApiResponse.<UserResponse>builder()
                    .code(202) // Đang chờ xác thực
                    .message("Đã gửi OTP xác thực đến email. Vui lòng xác thực để hoàn tất đăng ký.")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi gửi OTP: {}", e.getMessage(), e);
            return ApiResponse.<UserResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống khi gửi OTP")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<UserResponse> confirmCreateUser(String email, String otp) {
        // Xác thực OTP
        ApiResponse<String> verify = emailService.verifyOtp(email, otp);

        PendingRegistration pending = pendingRegistrationStorage.get(email);
        if (pending == null) {
            return ApiResponse.<UserResponse>builder()
                    .code(404)
                    .message("Không tìm thấy thông tin đăng ký tạm thời")
                    .build();
        }

        try {
            Users users = userMapper.toEntity(pending.getUserRequest());
            users.setRole(RoleStatus.USER.toString());
            users.setPassword(passwordEncoder.encode(users.getPassword()));
            users.setCreatedAt(LocalDateTime.now());

            if (pending.getAvatar() != null && !pending.getAvatar().isEmpty()) {
                try {
                    String avatarUrl = cloudinaryService.uploadFile(pending.getAvatar());
                    users.setAvatar(avatarUrl);
                } catch (Exception e) {
                    log.error("Lỗi upload ảnh đại diện: {}", e.getMessage(), e);
                    return ApiResponse.<UserResponse>builder()
                            .code(500)
                            .message("Tải ảnh đại diện thất bại")
                            .data(null)
                            .build();
                }
            }
            UserResponse response = userMapper.toResponse(userRepository.save(users));

            pendingRegistrationStorage.remove(email);

            return ApiResponse.<UserResponse>builder()
                    .code(200)
                    .message("Tạo user thành công")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi tạo user: {}", e.getMessage());
            return ApiResponse.<UserResponse>builder()
                    .code(500)
                    .message("Tạo user không thành công")
                    .data(null)
                    .build();
        }
    }
}
