package com.sparta.camp.java.FinalProject.domain.coupon.controller;

import com.sparta.camp.java.FinalProject.common.response.ApiResponse;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.response.MyCouponResponse;
import com.sparta.camp.java.FinalProject.domain.coupon.service.UserCouponService;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {
    private final UserCouponService userCouponService;

    @GetMapping("/my")
    public ApiResponse<List<MyCouponResponse>> getMyAvailableCoupons(@AuthenticationPrincipal User user) {
        return ApiResponse.success(userCouponService.getMyAvailableCoupons(user.getId()));
    }
}