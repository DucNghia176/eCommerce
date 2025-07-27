package ecommerce.userservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.UserRequest;
import ecommerce.userservice.dto.request.UserUpdateRequest;
import ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("create")
    public ApiResponse<UserResponse> createUser(
            @RequestPart("data") @Valid @ModelAttribute UserRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        return userService.createUser(request, avatar);
    }

    @GetMapping
    public ApiResponse<UserResponse> getById() {
        return userService.getUserById();
    }

    @PutMapping("update")
    public ApiResponse<UserResponse> updateUser(
            @RequestPart("data") @Valid @ModelAttribute UserUpdateRequest request,
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
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return userService.getAllUsers();
    }
}
