package ecommerce.userservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.UserInfoUpdateRequest;
import ecommerce.userservice.dto.respone.UserOrderDetail;
import ecommerce.userservice.dto.respone.UserOrdersResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


public interface UserService {
    ApiResponse<UserResponse> getUserInfoById();

    ApiResponse<UserResponse> updateUser(UserInfoUpdateRequest request, MultipartFile avatarFile);

    ApiResponse<UserResponse> toggleUserLock(Long id);

    ApiResponse<UserResponse> toggleUserRole(Long id);

//    ApiResponse<Page<UserResponse>> getAllUsers(int page, int size, Integer isLock);

//    ApiResponse<CountResponse> count();

    Map<Long, String> extractIds(List<Long> ids);

    ApiResponse<Page<UserOrdersResponse>> getUsersTOrders(int page, int size);

    ApiResponse<UserOrderDetail> getUserOrderDetail(Long id);
}