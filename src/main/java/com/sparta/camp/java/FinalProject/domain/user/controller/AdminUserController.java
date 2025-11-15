package com.sparta.camp.java.FinalProject.domain.user.controller;

import com.sparta.camp.java.FinalProject.common.annotation.Loggable;
import com.sparta.camp.java.FinalProject.common.response.ApiResponse;
import com.sparta.camp.java.FinalProject.domain.user.dto.UserResponse;
import com.sparta.camp.java.FinalProject.domain.user.dto.UserSearchResponse;
import com.sparta.camp.java.FinalProject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> findById(@PathVariable Long userId) {
        return ApiResponse.success(userService.getUserById(userId));
    }

}
