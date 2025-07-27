package ecommerce.orderservice.filter;

import ecommerce.aipcommon.model.status.RoleStatus;
import ecommerce.aipcommon.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    @Value("${jwt.signing-key}")
    private String signerKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            if (!jwtUtil.validateToken(jwt)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            try {
                Claims claims = jwtUtil.extractAllClaims(jwt);
                String userId = claims.getSubject();
                String roleStr = (String) claims.get("role");

                RoleStatus role = RoleStatus.valueOf(roleStr);

                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.name()));

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userId, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                System.out.println("Invalid JWT: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
