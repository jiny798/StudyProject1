package com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.request;

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
public class UpdateCouponRequest {
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
}