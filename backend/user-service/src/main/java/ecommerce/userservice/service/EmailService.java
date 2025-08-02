package ecommerce.userservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.userservice.email.OtpStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final OtpStorage otpStorage;

    public ApiResponse<String> sendOtp(String email) {
        String otp = generateOtp();
        otpStorage.storeOtp(email, otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Xác thực OTP");
        message.setText("Mã xác thực của bạn là: " + otp + "\nMã sẽ hết hạn sau 5 phút.");
        mailSender.send(message);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Đã gửi mã xác thực")
                .data(otp)
                .build();
    }

    public ApiResponse<String> verifyOtp(String email, String inputOtp) {
        boolean result = otpStorage.isValidOtp(email, inputOtp);
        if (result) {
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("Xác thực thành công.")
                    .data(null)
                    .build();
        } else {
            return ApiResponse.<String>builder()
                    .code(401)
                    .message("Mã OTP không hợp lệ hoặc đã hết hạn.")
                    .data(null)
                    .build();
        }
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900_000) + 100_000); // OTP 6 chữ số
    }
}
