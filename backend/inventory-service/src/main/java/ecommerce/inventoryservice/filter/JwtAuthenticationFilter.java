package ecommerce.inventoryservice.filter;

import ecommerce.aipcommon.model.status.RoleStatus;
import ecommerce.aipcommon.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    @Value("${jwt.signing-key}")
    private String signerKey;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Map<String, Object> claims = jwtUtil.validateAndExtractClaims(token, signerKey);

                String subject = (String) claims.get("sub"); // username/email
                String roleStr = (String) claims.get("role");

                RoleStatus role = RoleStatus.valueOf(roleStr); // ép thành enum (USER hoặc ADMIN)

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        subject,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(role.name())) // authority = "USER"/"ADMIN"
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (Exception e) {
                System.out.println("JWT không hợp lệ: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
