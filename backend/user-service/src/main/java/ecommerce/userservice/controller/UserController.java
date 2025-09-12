package ecommerce.userservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserResponse;
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
        return userService.getUserInfoById();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("update")
    public ApiResponse<UserResponse> updateUser(
            @RequestPart("data") @Valid UserInfoUpdateRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        return userService.updateUser(request, avatar);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/toggle/lock/{id}")
    public ApiResponse<UserResponse> toggleUserLock(@PathVariable Long id) {
        return userService.toggleUserLock(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/toggle/role/{id}")
    public ApiResponse<UserResponse> toggleUserRole(@PathVariable Long id) {
        return userService.toggleUserRole(id);
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
        return userService.getUsersTOrders(page, size);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/order/{userId}")
    public ApiResponse<UserOrderDetail> getUserOrderDetails(@PathVariable Long userId) {
        return userService.getUserOrderDetail(userId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{userId}")
    public ApiResponse<UserResponse> deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }
}
