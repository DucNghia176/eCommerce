package ecommerce.userservice.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import ecommerce.apicommon.model.response.ApiResponse;
import ecommerce.userservice.dto.request.AuthRequest;
import ecommerce.userservice.dto.respone.AuthResponse;
import ecommerce.userservice.entity.Users;
import ecommerce.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private static final String SIGNER_KEY = "/r/a3kh+1BLgXBAEU6dERcsXrzHZgWnsOqcnmxYDTxEMSa/6piUNFaoDWbmcE92K";
    private static final int TOKEN_EXPIRATION_HOURS = 24;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public String generateToken(Users users) {
        try {
            JWSObject jwsObject = new JWSObject(
                    new JWSHeader(JWSAlgorithm.HS256),
                    new Payload(
                            new JWTClaimsSet.Builder()
                                    .subject(users.getUsername())
                                    .issuer("deviate.com")
                                    .issueTime(new Date())
                                    .expirationTime(Date.from(Instant.now().plus(TOKEN_EXPIRATION_HOURS, ChronoUnit.HOURS)))
                                    .jwtID(UUID.randomUUID().toString())
                                    .claim("scope", buildScope(users))
                                    .build().toJSONObject()
                    )
            );
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Không thể tạo token", e);
        }
    }

    private String buildScope(Users users) {
        return Optional.ofNullable(users.getRole())
                .map(role -> role.toString())
                .orElse("");
    }

    public ApiResponse<AuthResponse> login(AuthRequest request) {
        try {
            Optional<Users> usersOpt = userRepository.findByUsername(request.getUsername());
            if (usersOpt.isEmpty()) {
                usersOpt = userRepository.findByEmail(request.getEmail());
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
            String token = generateToken(user);
            AuthResponse response = new AuthResponse();
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setPassword(user.getPassword() == null ? null : "********");
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
