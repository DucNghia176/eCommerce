package ecommerce.userservice.service;

import ecommerce.aipcommon.model.response.AuthResponse;
import ecommerce.userservice.dto.request.AuthRequest;
import ecommerce.userservice.dto.request.UserCreateRequest;
import ecommerce.userservice.dto.respone.UserCreateResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);

    UserCreateResponse createUser(UserCreateRequest request);

    UserCreateResponse confirmCreateUser(String email, String otp);
}
