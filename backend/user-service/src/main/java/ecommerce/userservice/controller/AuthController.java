package ecommerce.userservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.AuthResponse;
import ecommerce.userservice.dto.request.AuthRequest;
import ecommerce.userservice.dto.request.UserCreateRequest;
import ecommerce.userservice.dto.respone.UserCreateResponse;
import ecommerce.userservice.service.AuthService;
import ecommerce.userservice.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public ApiResponse<UserCreateResponse> createUser(
            @RequestPart("data") @Valid UserCreateRequest request) {
        return authService.createUser(request);
    }

    @PostMapping("/register/confirm")
    public ApiResponse<UserCreateResponse> confirmCreateUser(
            @RequestParam String email,
            @RequestParam String otp) {
        return authService.confirmCreateUser(email, otp);
    }

    @PostMapping("send")
    public ApiResponse<String> sendEmail(@RequestParam String email) {
        return emailService.sendOtp(email);
    }

    @PostMapping("/verify")
    public ApiResponse<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        return emailService.verifyOtp(email, otp);
    }

}
