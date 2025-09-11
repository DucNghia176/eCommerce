package ecommerce.userservice.service.impl;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.AuthResponse;
import ecommerce.aipcommon.model.status.RoleStatus;
import ecommerce.aipcommon.util.JwtUtil;
import ecommerce.userservice.dto.request.AuthRequest;
import ecommerce.userservice.dto.request.PendingRegistration;
import ecommerce.userservice.dto.request.UserCreateRequest;
import ecommerce.userservice.dto.respone.UserCreateResponse;
import ecommerce.userservice.email.PendingRegistrationStorage;
import ecommerce.userservice.entity.UserAcc;
import ecommerce.userservice.entity.UserInfo;
import ecommerce.userservice.mapper.UserAccMapper;
import ecommerce.userservice.repository.UserAccRepository;
import ecommerce.userservice.service.AuthService;
import ecommerce.userservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final PendingRegistrationStorage pendingRegistrationStorage;
    private final UserAccRepository userAccRepository;
    private final UserAccMapper userAccMapper;
    @Value("${jwt.signing-key}")
    private String SIGNER_KEY;

    @Override
    public ApiResponse<AuthResponse> login(AuthRequest request) {
        try {
            Optional<UserAcc> usersOpt = userAccRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail());
            if (usersOpt.isEmpty()) {
                return ApiResponse.<AuthResponse>builder()
                        .code(404)
                        .message("Tài khoản không tồn tại!")
                        .data(null)
                        .build();
            }
            UserAcc user = usersOpt.get();
            if (user.getIsLock() == 1) {
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
            response.setRole(user.getRole().toString());
            response.setToken(token);
            response.setLastLogin(String.valueOf(LocalDateTime.now()));

            user.setLastLogin(LocalDateTime.now());
            userAccRepository.save(user);

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
    public ApiResponse<UserCreateResponse> createUser(UserCreateRequest request) {
        try {
            if (userAccRepository.existsByUsername(request.getUsername())) {
                return ApiResponse.<UserCreateResponse>builder()
                        .code(400)
                        .message("Tên đăng nhập đã tồn tại")
                        .data(null)
                        .build();
            }

            if (userAccRepository.existsByEmail(request.getEmail())) {
                return ApiResponse.<UserCreateResponse>builder()
                        .code(400)
                        .message("Email đã tồn tại")
                        .data(null)
                        .build();
            }

            // Gửi OTP
            ApiResponse<String> otpResponse = emailService.sendOtp(request.getEmail());
            if (otpResponse.getCode() != 200) {
                return ApiResponse.<UserCreateResponse>builder()
                        .code(500)
                        .message("Không thể gửi OTP")
                        .build();
            }

            pendingRegistrationStorage.store(
                    request.getEmail(),
                    PendingRegistration.builder()
                            .userRequest(request)
                            .build()
            );

            UserCreateResponse response = UserCreateResponse.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .email(request.getEmail())
                    .build();

            return ApiResponse.<UserCreateResponse>builder()
                    .code(202) // Đang chờ xác thực
                    .message("Đã gửi OTP xác thực đến email. Vui lòng xác thực để hoàn tất đăng ký.")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi gửi OTP: {}", e.getMessage(), e);
            return ApiResponse.<UserCreateResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống khi gửi OTP")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<UserCreateResponse> confirmCreateUser(String email, String otp) {
        try {
            // Xác thực OTP
            ApiResponse<String> verify = emailService.verifyOtp(email, otp);

            if (verify.getCode() != 200) {
                // OTP sai hoặc hết hạn
                return ApiResponse.<UserCreateResponse>builder()
                        .code(400)
                        .message("OTP không hợp lệ hoặc đã hết hạn")
                        .build();
            }

            PendingRegistration pending = pendingRegistrationStorage.get(email);
            if (pending == null) {
                return ApiResponse.<UserCreateResponse>builder()
                        .code(404)
                        .message("Không tìm thấy thông tin đăng ký tạm thời")
                        .build();
            }


            UserAcc users = userAccMapper.toEntity(pending.getUserRequest());
            users.setRole(RoleStatus.USER);
            users.setPassword(passwordEncoder.encode(users.getPassword()));
            users.setCreatedAt(LocalDateTime.now());

            UserInfo userInfo = new UserInfo();
            userInfo.setUserAcc(users);
            users.setUserInfo(userInfo);

            UserCreateResponse response = userAccMapper.toResponse(userAccRepository.save(users));

            pendingRegistrationStorage.remove(email);

            return ApiResponse.<UserCreateResponse>builder()
                    .code(200)
                    .message("Tạo user thành công")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi tạo user: {}", e.getMessage());
            return ApiResponse.<UserCreateResponse>builder()
                    .code(500)
                    .message("Tạo user không thành công")
                    .data(null)
                    .build();
        }
    }
}
