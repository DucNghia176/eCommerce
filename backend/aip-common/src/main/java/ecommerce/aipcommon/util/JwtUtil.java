package ecommerce.aipcommon.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {
    private static final int TOKEN_EXPIRATION_HOURS = 24;
    @Value("${jwt.signing-key}")
    private String SIGNER_KEY;

    public String generateToken(Map<String, Object> claims, String subject, String signerKey) {
        try {
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issuer("deviate.com")
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plus(TOKEN_EXPIRATION_HOURS, ChronoUnit.HOURS)))
                    .jwtID(UUID.randomUUID().toString());

            // Gán tất cả claim custom
            if (claims != null) {
                claims.forEach(builder::claim);
            }

            JWTClaimsSet claimsSet = builder.build();

            JWSObject jwsObject = new JWSObject(
                    new JWSHeader(JWSAlgorithm.HS256),
                    new Payload(claimsSet.toJSONObject())
            );

            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();

        } catch (JOSEException e) {
            log.error("Lỗi tạo JWT", e);
            throw new IllegalStateException("Không thể tạo JWT", e);
        }
    }

    public Map<String, Object> validateAndExtractClaims(String token, String signerKey) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);

            JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
            if (!jwsObject.verify(verifier)) {
                throw new IllegalArgumentException("Chữ ký không hợp lệ");
            }

            JWTClaimsSet claims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

            if (claims.getExpirationTime() != null &&
                    claims.getExpirationTime().before(new Date())) {
                throw new IllegalArgumentException("Token đã hết hạn");
            }

            return claims.toJSONObject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Token không hợp lệ: " + e.getMessage(), e);
        }
    }

    public Long extractId(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            jwsObject.verify(new MACVerifier(SIGNER_KEY.getBytes()));

            Map<String, Object> payload = jwsObject.getPayload().toJSONObject();
            Number id = (Number) payload.get("userId");
            return id.longValue();
        } catch (Exception e) {
            throw new IllegalArgumentException("Token không hợp lệ hoặc không chứa ID", e);
        }
    }

    public String extractRole(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            jwsObject.verify(new MACVerifier(SIGNER_KEY.getBytes()));

            Map<String, Object> payload = jwsObject.getPayload().toJSONObject();
            return (String) payload.get("role");
        } catch (Exception e) {
            throw new IllegalArgumentException("Token không hợp lệ hoặc không chứa ID", e);
        }
    }
}
