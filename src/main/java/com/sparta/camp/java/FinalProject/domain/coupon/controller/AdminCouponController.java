package com.sparta.camp.java.FinalProject.domain.coupon.controller;

import com.sparta.camp.java.FinalProject.common.response.ApiResponse;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.request.CreateCouponRequest;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.request.IssueCouponRequest;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.response.CouponResponse;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.response.CouponResponseDto;
import com.sparta.camp.java.FinalProject.domain.coupon.service.AdminCouponService;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestPage;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/admin/coupons")
public class AdminCouponController {

    private final AdminCouponService adminCouponService;

    @PostMapping
    public ApiResponse<CouponResponse> create(@RequestBody CreateCouponRequest createCouponRequest) {
        CouponResponse response = adminCouponService.createCoupon(createCouponRequest);
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<PagingResponse<CouponResponseDto>> getList(@ModelAttribute RequestPage requestPage) {
        PagingResponse<CouponResponseDto> response = adminCouponService.getCouponList(requestPage);
        return ApiResponse.success(response);
    }


    @DeleteMapping("/{couponId}")
    public ApiResponse<CouponResponse> delete(@PathVariable("couponId") Long couponId) {
        adminCouponService.deleteCoupon(couponId);
        return ApiResponse.success();
    }

    @PostMapping("/issue")
    public ApiResponse<Void> issueCouponToUser(@RequestBody IssueCouponRequest request) {
        adminCouponService.issueCouponToUser(request.getUserId(), request.getCouponId());
        return ApiResponse.success();
    }
}