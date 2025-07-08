package ecommerce.userservice.controller;

import ecommerce.apicommon.model.response.ApiResponse;
import ecommerce.apicommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.UserRequest;
import ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("create")
    public ApiResponse<UserResponse> createUser(@RequestPart("data") @Valid @ModelAttribute UserRequest request,
                                                @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        return userService.createUser(request, avatar);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
