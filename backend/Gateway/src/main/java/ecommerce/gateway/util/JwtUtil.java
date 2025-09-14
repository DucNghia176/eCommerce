package ecommerce.gateway.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {

    private static final String SECRET_KEY = "/r/a3kh+1BLgXBAEU6dERcsXrzHZgWnsOqcnmxYDTxEMSa/6piUNFaoDWbmcE92K";
    private static final int EXPIRATION_HOURS = 10;

    public String generateToken(Map<String, Object> claims, String subject) {
        try {
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issuer("gateway-service")
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plus(EXPIRATION_HOURS, ChronoUnit.HOURS)))
                    .jwtID(UUID.randomUUID().toString());

            if (claims != null) {
                claims.forEach(builder::claim);
            }

            JWTClaimsSet claimsSet = builder.build();

            JWSObject jwsObject = new JWSObject(
                    new JWSHeader(JWSAlgorithm.HS256),
                    new Payload(claimsSet.toJSONObject())
            );

            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Lỗi tạo JWT: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể tạo token", e);
        }
    }

    public JWTClaimsSet extractAllClaims(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

            if (!jwsObject.verify(verifier)) {
                throw new IllegalArgumentException("Chữ ký token không hợp lệ");
            }

            JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

            if (claimsSet.getExpirationTime() != null &&
                    claimsSet.getExpirationTime().before(new Date())) {
                throw new IllegalArgumentException("Token đã hết hạn");
            }

            return claimsSet;
        } catch (Exception e) {
            throw new IllegalArgumentException("Token không hợp lệ: " + e.getMessage(), e);
        }
    }

    public List<String> extractRoles(String token) {
        Object rolesObj = extractAllClaims(token).getClaim("role");
        if (rolesObj instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }
        return List.of(); // trả về rỗng nếu không có role
    }

    public Long extractUserId(String token) {
        Object id = extractAllClaims(token).getClaim("userId");
        return id != null ? Long.parseLong(id.toString()) : null;
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
