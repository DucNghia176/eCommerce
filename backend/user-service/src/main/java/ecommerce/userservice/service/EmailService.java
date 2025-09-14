package ecommerce.userservice.service;

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

    public String sendOtp(String email) {
        String otp = generateOtp();
        otpStorage.storeOtp(email, otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Xác thực OTP");
        message.setText("Mã xác thực của bạn là: " + otp + "\nMã sẽ hết hạn sau 5 phút.");
        mailSender.send(message);
        return otp;
    }

    public boolean verifyOtp(String email, String inputOtp) {
        return otpStorage.isValidOtp(email, inputOtp);
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900_000) + 100_000); // OTP 6 chữ số
    }
}
