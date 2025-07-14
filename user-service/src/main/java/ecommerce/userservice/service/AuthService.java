package ecommerce.userservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.AuthResponse;
import ecommerce.userservice.dto.request.AuthRequest;

public interface AuthService {


    public ApiResponse<AuthResponse> login(AuthRequest request);
}
