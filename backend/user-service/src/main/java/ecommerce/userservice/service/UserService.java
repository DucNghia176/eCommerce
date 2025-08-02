package ecommerce.userservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface UserService {
    ApiResponse<UserResponse> getUserById();

    ApiResponse<UserResponse> updateUser(UserUpdateRequest request, MultipartFile avatarFile);

    ApiResponse<UserResponse> toggleUserLock(Long id);

    ApiResponse<UserResponse> toggleUserRole(Long id);

    ApiResponse<List<UserResponse>> getAllUsers();
}