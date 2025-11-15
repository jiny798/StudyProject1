package com.sparta.camp.java.FinalProject.domain.coupon.controller.dto;

import com.sparta.camp.java.FinalProject.common.response.ApiResponse;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.request.UseCouponRequest;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.response.UserCouponResponse;
import com.sparta.camp.java.FinalProject.domain.coupon.service.CouponService;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/users/coupons")
    public ApiResponse<List<UserCouponResponse>> getUserCoupons(@AuthenticationPrincipal User user) {
        List<UserCouponResponse> coupons = couponService.getUserCoupons(user.getId());
        return ApiResponse.success(coupons);
    }


}