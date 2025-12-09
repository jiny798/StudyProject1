package com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.response;

import com.sparta.camp.java.FinalProject.domain.coupon.entity.Coupon;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.CouponStatus;
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
public class CouponResponseDto {

    private Long id;
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
    private Integer issuedQuantity; // 현재까지 발급된 수량 (관리자 화면에서 유용)

    private CouponStatus status;
    private LocalDateTime createdAt;

    public CouponResponseDto(Coupon coupon) {
        this.id = coupon.getId();
        this.name = coupon.getName();
        this.description = coupon.getDescription();
        this.discountType = coupon.getDiscountType();
        this.discountValue = coupon.getDiscountValue();
        this.expirationType = coupon.getExpirationType();
        this.validDays = coupon.getValidDays();
        this.startDate = coupon.getStartDate();
        this.endDate = coupon.getEndDate();
        this.minOrderAmount = coupon.getMinOrderAmount();
        this.maxDiscountAmount = coupon.getMaxDiscountAmount();
        this.totalQuantity = coupon.getTotalQuantity();
        this.issuedQuantity = coupon.getIssuedQuantity();
        this.status = coupon.getStatus();
        this.createdAt = coupon.getCreatedAt();
    }
}