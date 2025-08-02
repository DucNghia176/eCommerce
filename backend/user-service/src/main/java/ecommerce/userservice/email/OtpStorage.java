package ecommerce.userservice.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class OtpStorage {
    private static final long EXPIRATION_MINUTES = 1;
    private final Map<String, OtpEntry> otpMap = new HashMap<>();

    public void storeOtp(String email, String otp) {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        otpMap.put(email, new OtpEntry(otp, expiresAt));
    }

    public boolean isValidOtp(String email, String inputOtp) {
        OtpEntry otpEntry = otpMap.get(email);
        if (otpEntry == null) {
            return false;
        }
        boolean isValid = otpEntry.getOtp().equals(inputOtp) &&
                otpEntry.getExpiresAt().isAfter(LocalDateTime.now());
        if (isValid) {
            otpMap.remove(email);
        }
        return isValid;
    }

    @Data
    @AllArgsConstructor
    static class OtpEntry {
        private String otp;
        private LocalDateTime expiresAt;
    }
}
