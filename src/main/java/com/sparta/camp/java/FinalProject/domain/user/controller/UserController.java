package com.sparta.camp.java.FinalProject.domain.user.controller;

import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.common.response.ApiResponse;
import com.sparta.camp.java.FinalProject.domain.user.dto.UserCreateRequest;
import com.sparta.camp.java.FinalProject.domain.user.dto.UserPasswordUpdateRequest;
import com.sparta.camp.java.FinalProject.domain.user.dto.UserResponse;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import com.sparta.camp.java.FinalProject.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> signUp(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.signUp(request));
    }

    @PutMapping("/password")
    public ApiResponse<Void> updatePassword(@AuthenticationPrincipal User user,
                                    @Valid @RequestBody UserPasswordUpdateRequest request) {
        userService.updatePassword(user.getId(), request);
        return ApiResponse.success();
    }
    @DeleteMapping("{userId}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal User user) {
        userService.delete(user.getId());
        return ApiResponse.success();
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe(@AuthenticationPrincipal User user) {
        if (user == null) {
            throw new ServiceException(ServiceExceptionCode.UNAUTHORIZED);
        }

        UserResponse userResponse = userService.getUserById(user.getId());
        return ApiResponse.success(userResponse);
    }

}