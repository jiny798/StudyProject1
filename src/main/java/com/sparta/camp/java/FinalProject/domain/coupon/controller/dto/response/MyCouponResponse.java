package com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.response;

import com.sparta.camp.java.FinalProject.domain.coupon.entity.UserCoupon;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.type.DiscountType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class MyCouponResponse {

    private final Long userCouponId;
    private final String name;
    private final String description;
    private final DiscountType discountType;
    private final BigDecimal discountValue;
    private final boolean isUsed;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expirationDate;

    public static MyCouponResponse from(UserCoupon userCoupon) {
        return MyCouponResponse.builder()
                .userCouponId(userCoupon.getId())
                .name(userCoupon.getCoupon().getName())
                .description(userCoupon.getCoupon().getDescription())
                .discountType(userCoupon.getCoupon().getDiscountType())
                .discountValue(userCoupon.getCoupon().getDiscountValue())
                .isUsed(userCoupon.isUsed())
                .issuedAt(userCoupon.getIssuedAt())
                .expirationDate(userCoupon.getExpirationDate())
                .build();
    }
}