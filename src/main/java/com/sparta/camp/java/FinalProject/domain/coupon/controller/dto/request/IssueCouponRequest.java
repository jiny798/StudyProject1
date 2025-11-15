package com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IssueCouponRequest {
    private Long userId;
    private Long couponId;
}