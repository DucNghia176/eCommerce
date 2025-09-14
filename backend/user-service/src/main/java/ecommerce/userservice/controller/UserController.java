package ecommerce.userservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.AddRoleRequest;
import ecommerce.userservice.dto.request.UserInfoUpdateRequest;
import ecommerce.userservice.dto.respone.UserOrderDetail;
import ecommerce.userservice.dto.respone.UserOrdersResponse;
import ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById() {
        UserResponse userResponse = userService.getUserInfoById();
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Lấy thông tin người dùng thành công")
                .data(userResponse)
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("update")
    public ApiResponse<UserResponse> updateUser(
            @RequestPart("data") @Valid UserInfoUpdateRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        UserResponse userResponse = userService.updateUser(request, avatar);
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Cập nhật thành công ")
                .data(userResponse)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/toggle/lock/{id}")
    public ApiResponse<UserResponse> toggleUserLock(@PathVariable Long id) {
        UserResponse userResponse = userService.toggleUserLock(id);
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Thay đổi trạng thái thành công")
                .data(userResponse)
                .build();
    }

//    @GetMapping("/all")
//    public ApiResponse<Page<UserResponse>> getAllUsers(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) Integer isLock
//    ) {
//        return userService.getAllUsers(page, size, isLock);
//    }

    @GetMapping("/ids")
    public Map<Long, String> extractFullName(@RequestParam("ids") List<Long> ids) {
        return userService.extractIds(ids);
    }

//    @GetMapping("/count")
//    public ApiResponse<CountResponse> count() {
//        return userService.count();
//    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all/1")
    public ApiResponse<Page<UserOrdersResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        Page<UserOrdersResponse> userOrdersResponse = userService.getUsersTOrders(page, size);
        return ApiResponse.<Page<UserOrdersResponse>>builder()
                .code(200)
                .message("Lấy thành công")
                .data(userOrdersResponse)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/order/{userId}")
    public ApiResponse<UserOrderDetail> getUserOrderDetails(@PathVariable Long userId) {
        UserOrderDetail userOrderDetail = userService.getUserOrderDetail(userId);
        return ApiResponse.<UserOrderDetail>builder()
                .code(200)
                .data(userOrderDetail)
                .message("Thành công")
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{userId}")
    public ApiResponse<UserResponse> deleteUser(@PathVariable Long userId) {
        UserResponse userResponse = userService.deleteUser(userId);
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Xóa người dùng thành công")
                .data(userResponse)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/addRoles")
    public ApiResponse<UserResponse> addRoles(@RequestBody AddRoleRequest request) {
        UserResponse userResponse = userService.addRoleToUser(request);
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Thêm role thành công")
                .data(userResponse)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/removeRoles")
    public ApiResponse<UserResponse> removeRoles(@RequestBody AddRoleRequest request) {
        UserResponse userResponse = userService.addRoleToUser(request);
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Thêm role thành công")
                .data(userResponse)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/search/jpa")
    public ApiResponse<List<UserResponse>> searchJPA(@RequestParam String name, @RequestParam String gender, @RequestParam Integer isLock, @RequestParam String email) {
        List<UserResponse> userResponse = userService.searchUsersJPA(name, gender, isLock, email);

        return ApiResponse.<List<UserResponse>>builder()
                .code(200)
                .message("Tìm kiếm thành công")
                .data(userResponse)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/search/jdbc")
    public ApiResponse<List<UserResponse>> searchJDBC(@RequestParam String name, @RequestParam String gender, @RequestParam Integer isLock, @RequestParam String email) {
        List<UserResponse> userResponse = userService.searchUsersJDBC(name, gender, isLock, email);

        return ApiResponse.<List<UserResponse>>builder()
                .code(200)
                .message("Tìm kiếm thành công")
                .data(userResponse)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/search/jdbcNamed")
    public ApiResponse<List<UserResponse>> searchJDBCNamed(@RequestParam String name, @RequestParam String gender, @RequestParam Integer isLock, @RequestParam String email) {
        List<UserResponse> userResponse = userService.searchUsersJdbcNamed(name, gender, isLock, email);

        return ApiResponse.<List<UserResponse>>builder()
                .code(200)
                .message("Tìm kiếm thành công")
                .data(userResponse)
                .build();
    }
}
