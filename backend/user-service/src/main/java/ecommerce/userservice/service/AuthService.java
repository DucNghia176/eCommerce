package ecommerce.userservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.AuthResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.AuthRequest;
import ecommerce.userservice.dto.request.UserRequest;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    ApiResponse<AuthResponse> login(AuthRequest request);

    ApiResponse<UserResponse> createUser(UserRequest request, MultipartFile avatarFile);

    ApiResponse<UserResponse> confirmCreateUser(String email, String otp);
}
