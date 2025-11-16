package com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueCouponRequest {
    private Long userId;
    private Long couponId;
}