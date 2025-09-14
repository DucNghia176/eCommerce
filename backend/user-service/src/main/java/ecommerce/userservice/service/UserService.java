package ecommerce.userservice.service;

import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.AddRoleRequest;
import ecommerce.userservice.dto.request.UserInfoUpdateRequest;
import ecommerce.userservice.dto.respone.UserOrderDetail;
import ecommerce.userservice.dto.respone.UserOrdersResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


public interface UserService {
    UserResponse getUserInfoById();

    UserResponse updateUser(UserInfoUpdateRequest request, MultipartFile avatarFile);

    UserResponse toggleUserLock(Long id);

//    ApiResponse<Page<UserResponse>> getAllUsers(int page, int size, Integer isLock);

//    ApiResponse<CountResponse> count();

    Map<Long, String> extractIds(List<Long> ids);

    Page<UserOrdersResponse> getUsersTOrders(int page, int size);

    UserOrderDetail getUserOrderDetail(Long id);

    UserResponse deleteUser(Long id);

    UserResponse addRoleToUser(AddRoleRequest request);
}