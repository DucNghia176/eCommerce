package ecommerce.userservice.config;

import ecommerce.apicommon1.model.status.RoleStatus;
import ecommerce.apicommon1.util.JwtUtil;
import ecommerce.userservice.entity.Role;
import ecommerce.userservice.entity.UserAcc;
import ecommerce.userservice.entity.UserInfo;
import ecommerce.userservice.repository.RoleRepository;
import ecommerce.userservice.repository.UserAccRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final UserAccRepository userAccRepository;
    private final RoleRepository roleRepository;
    @Value("${jwt.signing-key}")
    private String SIGNER_KEY;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken idToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = idToken.getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = idToken.getPrincipal().getAttributes();

        String oauth2Id;
        String fullName;
        String email;

        if ("google".equals(registrationId)) {
            oauth2Id = (String) attributes.get("sub");
            fullName = (String) attributes.get("name");
            email = (String) attributes.get("email");
        } else if ("facebook".equals(registrationId)) {
            oauth2Id = (String) attributes.get("id");
            fullName = (String) attributes.get("name");
            email = (String) attributes.get("email");
        } else {
            throw new RuntimeException("Provider không hỗ trợ");
        }
        String username = "user" + UUID.randomUUID().toString().substring(0, 8);

        UserAcc user;
        if ("google".equals(registrationId)) {
            user = userAccRepository.findByGoogleId(oauth2Id)
                    .orElseGet(() -> handleExistingEmail(email, "google", oauth2Id, username, fullName));
        } else { // facebook
            user = userAccRepository.findByFacebookId(oauth2Id)
                    .orElseGet(() -> handleExistingEmail(email, "facebook", oauth2Id, username, fullName));
        }

        if (user.getIsLock() == 1) {
            throw new AccessDeniedException("Tài khoản đã bị khóa");
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

        user.setLastLogin(LocalDateTime.now());
        userAccRepository.save(user);

        String origin = request.getHeader("Origin");
        if (origin == null) {
            origin = "http://localhost:4200"; // fallback mặc định
        }

        response.sendRedirect(origin + "/auth/login-success?token=" + token);
    }

    private UserAcc handleExistingEmail(String email, String provider, String oauth2Id,
                                        String username, String fullName) {
        Optional<UserAcc> existingUserOpt = userAccRepository.findByEmail(email);
        if (existingUserOpt.isPresent()) {
            UserAcc existingUser = existingUserOpt.get();
            if ("google".equals(provider)) existingUser.setGoogleId(oauth2Id);
            else if ("facebook".equals(provider)) existingUser.setFacebookId(oauth2Id);
            return userAccRepository.save(existingUser);
        }

        // Nếu email chưa tồn tại, tạo mới
        UserAcc userAcc = new UserAcc();
        userAcc.setUsername(username);
        userAcc.setEmail(email);
        if ("google".equals(provider)) userAcc.setGoogleId(oauth2Id);
        else if ("facebook".equals(provider)) userAcc.setFacebookId(oauth2Id);

        UserInfo info = new UserInfo();
        info.setFullName(fullName);
        info.setUserAcc(userAcc);
        userAcc.setUserInfo(info);

        Role userRole = roleRepository.findByRoleName(RoleStatus.USER)
                .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));
        userAcc.getRoles().add(userRole);

        return userAccRepository.save(userAcc);
    }
}
