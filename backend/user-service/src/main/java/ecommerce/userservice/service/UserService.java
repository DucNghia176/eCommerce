package ecommerce.userservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.UserUpdateRequest;
import ecommerce.userservice.dto.respone.CountResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {
    ApiResponse<UserResponse> getUserById();

    ApiResponse<UserResponse> updateUser(UserUpdateRequest request, MultipartFile avatarFile);

    ApiResponse<UserResponse> toggleUserLock(Long id);

    ApiResponse<UserResponse> toggleUserRole(Long id);

    ApiResponse<Page<UserResponse>> getAllUsers(int page, int size, Integer isLock);

    ApiResponse<CountResponse> count();
}