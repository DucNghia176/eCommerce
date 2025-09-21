package ecommerce.userservice.service.impl;

import ecommerce.apicommon1.model.response.AuthResponse;
import ecommerce.apicommon1.model.status.RoleStatus;
import ecommerce.apicommon1.util.JwtUtil;
import ecommerce.userservice.dto.request.AuthRequest;
import ecommerce.userservice.dto.request.PendingRegistration;
import ecommerce.userservice.dto.request.UserCreateRequest;
import ecommerce.userservice.dto.respone.UserCreateResponse;
import ecommerce.userservice.email.PendingRegistrationStorage;
import ecommerce.userservice.entity.Role;
import ecommerce.userservice.entity.UserAcc;
import ecommerce.userservice.entity.UserInfo;
import ecommerce.userservice.mapper.UserAccMapper;
import ecommerce.userservice.repository.RoleRepository;
import ecommerce.userservice.repository.UserAccRepository;
import ecommerce.userservice.service.AuthService;
import ecommerce.userservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
    private final RoleRepository roleRepository;
    @Value("${jwt.signing-key}")
    private String SIGNER_KEY;

    @Override
    public AuthResponse login(AuthRequest request) {
        UserAcc user = userAccRepository.
                findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new NoSuchElementException("Tài khoản không tồn tại"));
        if (user.getIsLock() == 1) {
            throw new AccessDeniedException("Tài khoản đã bị khóa!");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Sai mật khẩu!");
        }

        List<String> roleNames = user.getRoles()
                .stream()
                .map(role -> role.getRoleName().toString())
                .toList();

        Map<String, Object> claims = Map.of(
                "userId", user.getId(),
                "role", roleNames);
        String subject = user.getUsername() + "/" + user.getEmail();
        String token = jwtUtil.generateToken(claims, subject, SIGNER_KEY);

        AuthResponse response = AuthResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(roleNames)
                .token(token)
                .lastLogin(LocalDateTime.now())
                .build();

        user.setLastLogin(LocalDateTime.now());
        userAccRepository.save(user);

        return response;
    }

//    @Override
//    public AuthResponse loginOauth2(String idToken) {
//        Map<String, Object> userInfo = verifyIdTokenWithGoogle(idToken);
//        String oauth2Id = (String) userInfo.get("sub");
//        String username = (String) userInfo.get("name");
//        String email = (String) userInfo.get("email");
//
//        UserAcc user = userAccRepository.findByOauth2Id(oauth2Id)
//                .orElseGet(() -> {
//                    UserAcc userAcc = new UserAcc();
//                    userAcc.setOauth2Id(oauth2Id);
//                    userAcc.setUsername(username);
//                    userAcc.setEmail(email);
//
//                    Role userRole = roleRepository.findByRoleName(RoleStatus.USER)
//                            .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));
//
//                    userAcc.getRoles().add(userRole);
//                    return userAccRepository.save(userAcc);
//                });
//
//        if (user.getIsLock() == 1) {
//            throw new AccessDeniedException("Tài khoản đã bị khóa");
//        }
//
//        List<String> roleNames = user.getRoles()
//                .stream()
//                .map(role -> role.getRoleName().toString())
//                .toList();
//
//        Map<String, Object> claims = Map.of(
//                "userId", user.getId(),
//                "role", roleNames
//        );
//        String subject = user.getUsername() + "/" + user.getEmail();
//        String token = jwtUtil.generateToken(claims, subject, SIGNER_KEY);
//
//        AuthResponse response = AuthResponse.builder()
//                .username(username)
//                .email(email)
//                .role(roleNames)
//                .token(token)
//                .lastLogin(LocalDateTime.now())
//                .build();
//
//        user.setLastLogin(LocalDateTime.now());
//        userAccRepository.save(user);
//
//        return response;
//    }

    private Map<String, Object> verifyIdTokenWithGoogle(String idToken) {
        // Gọi Google tokeninfo endpoint
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("sub")) {
            throw new IllegalArgumentException("Invalid ID token");
        }
        return response;
    }

    @Transactional
    @Override
    public UserCreateResponse createUser(UserCreateRequest request) {
        if (userAccRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        if (userAccRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        String otp = emailService.sendOtp(request.getEmail());
        if (otp == null) {
            throw new RuntimeException("Không thể gửi OTP");
        }

        pendingRegistrationStorage.store(
                request.getEmail(),
                PendingRegistration.builder()
                        .userRequest(request)
                        .build()
        );

        return UserCreateResponse.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .build();
    }

    @Transactional
    @Override
    public UserCreateResponse confirmCreateUser(String email, String otp) {
        // Xác thực OTP
        boolean verify = emailService.verifyOtp(email, otp);

        if (!verify) {
            throw new IllegalArgumentException("OTP không hợp lệ hoặc đã hết hạn");
        }

        PendingRegistration pending = pendingRegistrationStorage.get(email);
        if (pending == null) {
            throw new NoSuchElementException("Không tìm thấy thông tin đăng ký tạm thời");
        }

        UserAcc users = userAccMapper.toEntity(pending.getUserRequest());
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users.setCreatedAt(LocalDateTime.now());
        addRole(users, RoleStatus.USER);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserAcc(users);
        userInfo.setUserAcc(users);

        users.setUserInfo(userInfo);

        UserCreateResponse response = userAccMapper.toResponse(userAccRepository.save(users));

        pendingRegistrationStorage.remove(email);

        return response;
    }

    public void addRole(UserAcc user, RoleStatus status) {
        Role role = roleRepository.findByRoleName(status)
                .orElseThrow(() -> new RuntimeException("Role " + status + " không tồn tại"));
        user.getRoles().add(role);
    }
}
