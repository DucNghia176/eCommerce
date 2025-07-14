package ecommerce.userservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.UserRequest;
import ecommerce.userservice.dto.request.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {
    ApiResponse<UserResponse> createUser(UserRequest request, MultipartFile avatarFile);

    ApiResponse<UserResponse> getUserById();

    ApiResponse<UserResponse> updateUser(UserUpdateRequest request, MultipartFile avatarFile);

    ApiResponse<UserResponse> toggleUserLock(Long id);

    ApiResponse<UserResponse> toggleUserRole(Long id);
}