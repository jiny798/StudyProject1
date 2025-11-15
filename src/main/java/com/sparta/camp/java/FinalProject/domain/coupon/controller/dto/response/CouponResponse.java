package com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.response;

import com.sparta.camp.java.FinalProject.domain.coupon.entity.Coupon;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.CouponStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CouponResponse {
    private Long id;
    private String name;
    private CouponStatus status;
    private BigDecimal discountValue;
    private Integer totalQuantity;

    public static CouponResponse from(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .status(coupon.getStatus())
                .discountValue(coupon.getDiscountValue())
                .totalQuantity(coupon.getTotalQuantity())
                .build();
    }
}