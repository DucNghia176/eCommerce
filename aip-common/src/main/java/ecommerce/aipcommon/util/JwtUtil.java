package ecommerce.aipcommon.util;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.MACVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.signing-key}")
    private String SIGNER_KEY;

    public Long extractId(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            jwsObject.verify(new MACVerifier(SIGNER_KEY.getBytes()));

            Map<String, Object> payload = jwsObject.getPayload().toJSONObject();
            Number id = (Number) payload.get("id");
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
