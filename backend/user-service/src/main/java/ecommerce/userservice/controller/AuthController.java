package ecommerce.userservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.AuthResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.AuthRequest;
import ecommerce.userservice.dto.request.UserRequest;
import ecommerce.userservice.service.AuthService;
import ecommerce.userservice.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "register", consumes = {"multipart/form-data", "multipart/form-data;charset=UTF-8"})
    public ApiResponse<UserResponse> createUser(
            @RequestPart("data") @Valid UserRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        return authService.createUser(request, avatar);
    }

    @PostMapping("/register/confirm")
    public ApiResponse<UserResponse> confirmCreateUser(
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
