package ecommerce.userservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.AuthResponse;
import ecommerce.userservice.dto.request.AuthRequest;
import ecommerce.userservice.dto.request.UserCreateRequest;
import ecommerce.userservice.dto.respone.UserCreateResponse;

public interface AuthService {
    ApiResponse<AuthResponse> login(AuthRequest request);

    ApiResponse<UserCreateResponse> createUser(UserCreateRequest request);

    ApiResponse<UserCreateResponse> confirmCreateUser(String email, String otp);
}
