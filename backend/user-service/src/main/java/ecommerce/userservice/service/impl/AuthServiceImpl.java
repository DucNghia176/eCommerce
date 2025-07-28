package ecommerce.userservice.service.impl;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.AuthResponse;
import ecommerce.aipcommon.util.JwtUtil;
import ecommerce.userservice.dto.request.AuthRequest;
import ecommerce.userservice.entity.Users;
import ecommerce.userservice.repository.UserRepository;
import ecommerce.userservice.service.AuthService;
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
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
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
}
