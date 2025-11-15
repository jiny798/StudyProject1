package com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.response;

import com.sparta.camp.java.FinalProject.domain.coupon.entity.UserCoupon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class UserCouponResponse {

    private final Long userCouponId;        // 유저 쿠폰 ID
    private final String couponName;        // 쿠폰 이름
    private final String couponDescription; // 쿠폰 설명
    private final LocalDateTime expirationDate;    // 만료일
    private final boolean isUsed;           // 사용 여부

}