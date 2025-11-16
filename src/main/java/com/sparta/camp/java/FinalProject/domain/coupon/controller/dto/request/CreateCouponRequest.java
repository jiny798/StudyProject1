package com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.request;

import com.sparta.camp.java.FinalProject.domain.coupon.entity.Coupon;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.type.DiscountType;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.type.ExpirationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCouponRequest {
    private String name;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private ExpirationType expirationType;
    private Integer validDays;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private Integer totalQuantity;

    public Coupon toEntity() {
        return Coupon.builder()
                .name(name)
                .description(description)
                .discountType(discountType)
                .discountValue(discountValue)
                .expirationType(expirationType)
                .validDays(validDays)
                .startDate(startDate)
                .endDate(endDate)
                .minOrderAmount(minOrderAmount)
                .maxDiscountAmount(maxDiscountAmount)
                .totalQuantity(totalQuantity)
                .build();
    }
}