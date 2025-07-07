package ecommerce.userservice.controller;

import ecommerce.apicommon.model.response.ApiResponse;
import ecommerce.apicommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.UserRequest;
import ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("create")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
