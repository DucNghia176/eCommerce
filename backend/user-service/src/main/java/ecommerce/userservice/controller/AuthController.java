package ecommerce.userservice.controller;


import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.response.AuthResponse;
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
        AuthResponse authResponse = authService.login(request);
        return ApiResponse.<AuthResponse>builder()
                .code(200)
                .message("Đăng nhập thành công")
                .data(authResponse)
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<UserCreateResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserCreateResponse userCreateResponse = authService.createUser(request);
        return ApiResponse.<UserCreateResponse>builder()
                .code(202)
                .message("Đã gửi OTP xác thực đến email. Vui lòng xác thực để hoàn tất đăng ký.")
                .data(userCreateResponse)
                .build();
    }

    @PostMapping("/register/confirm")
    public ApiResponse<UserCreateResponse> confirmCreateUser(@RequestParam String email, @RequestParam String otp) {
        UserCreateResponse userCreateResponse = authService.confirmCreateUser(email, otp);
        return ApiResponse.<UserCreateResponse>builder()
                .code(200)
                .message("Tạo user thành công")
                .data(userCreateResponse)
                .build();

    }

    @PostMapping("send")
    public ApiResponse<String> sendEmail(@RequestParam String email) {
        String otp = emailService.sendOtp(email);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Đã gửi mã OTP")
                .data(otp)
                .build();
    }

    @PostMapping("/verify")
    public ApiResponse<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean result = emailService.verifyOtp(email, otp);
        if (result) {
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("Xác thực thành công")
                    .data(null)
                    .build();
        } else {
            return ApiResponse.<String>builder()
                    .code(401)
                    .message("Mã OTP không hợp lệ hoặc đã hết hạn")
                    .data(null)
                    .build();
        }
    }

}
