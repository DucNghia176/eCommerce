package ecommerce.userservice.service;

import ecommerce.apicommon1.model.response.AuthResponse;
import ecommerce.userservice.dto.request.AuthRequest;
import ecommerce.userservice.dto.request.UserCreateRequest;
import ecommerce.userservice.dto.respone.UserCreateResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);

//    AuthResponse loginOauth2(String idToken);

    UserCreateResponse createUser(UserCreateRequest request);

    UserCreateResponse confirmCreateUser(String email, String otp);
}
