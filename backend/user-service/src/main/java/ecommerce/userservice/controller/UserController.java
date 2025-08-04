package ecommerce.userservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.UserUpdateRequest;
import ecommerce.userservice.dto.respone.CountResponse;
import ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<UserResponse> getById() {
        return userService.getUserById();
    }

    @PutMapping("update")
    public ApiResponse<UserResponse> updateUser(
            @RequestPart("data") @Valid UserUpdateRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        return userService.updateUser(request, avatar);
    }

    @PutMapping("/toggle/lock/{id}")
    public ApiResponse<UserResponse> toggleUserLock(@PathVariable Long id) {
        return userService.toggleUserLock(id);
    }

    @PutMapping("/toggle/role/{id}")
    public ApiResponse<UserResponse> toggleUserRole(@PathVariable Long id) {
        return userService.toggleUserRole(id);
    }

    @GetMapping("/all")
    public ApiResponse<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer isLock
    ) {
        return userService.getAllUsers(page, size, isLock);
    }

    @GetMapping("/count")
    public ApiResponse<CountResponse> count() {
        return userService.count();
    }
}
