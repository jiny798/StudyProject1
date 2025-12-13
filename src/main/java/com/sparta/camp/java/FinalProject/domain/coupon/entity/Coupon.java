package com.sparta.camp.java.FinalProject.domain.coupon.entity;

import com.google.gson.stream.JsonWriter;
import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.request.UpdateCouponRequest;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.type.DiscountType;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.type.ExpirationType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 300)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Enumerated(EnumType.STRING)
    private ExpirationType expirationType;

    private Integer validDays;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    private Integer totalQuantity;

    private Integer issuedQuantity;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    LocalDateTime updatedAt;

    @Builder
    Coupon(String name,
           String description,
           DiscountType discountType,
           BigDecimal discountValue,
           ExpirationType expirationType,
           Integer validDays,
           LocalDateTime startDate,
           LocalDateTime endDate,
           BigDecimal minOrderAmount,
           BigDecimal maxDiscountAmount,
           Integer totalQuantity
    ) {
        this.name = name;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.expirationType = expirationType;
        this.validDays = validDays;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.totalQuantity = totalQuantity;
        this.status = CouponStatus.ACTIVE;
        this.issuedQuantity = 0;
    }

    public void update(UpdateCouponRequest request) {
        this.name = request.getName();
        this.description = request.getDescription();
        this.discountType = request.getDiscountType();
        this.discountValue = request.getDiscountValue();
        this.expirationType = request.getExpirationType();
        this.validDays = request.getValidDays();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.minOrderAmount = request.getMinOrderAmount();
        this.maxDiscountAmount = request.getMaxDiscountAmount();
        this.totalQuantity = request.getTotalQuantity();
    }

    public void activate() {
        this.status = CouponStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = CouponStatus.INACTIVE;
    }

    public void validateTemplate() {
        if (expirationType == ExpirationType.DATE_RANGE) {
            if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
                throw new ServiceException(ServiceExceptionCode.INVALID_COUPON_DATE_RANGE);
            }
        } else if (expirationType == ExpirationType.VALID_DAYS_ON_ISSUE) {
            if (validDays == null || validDays <= 0) {
                throw new ServiceException(ServiceExceptionCode.INVALID_COUPON_VALID_DAYS);
            }
        }
    }

    public void validateIssuable() {
        if (this.status != CouponStatus.ACTIVE) {
            throw new ServiceException(ServiceExceptionCode.COUPON_NOT_ACTIVE);
        }
        if (this.totalQuantity != null && this.issuedQuantity >= this.totalQuantity) {
            throw new ServiceException(ServiceExceptionCode.COUPON_OUT_OF_STOCK);
        }
    }

    public void decreaseQuantity() {
        if (this.totalQuantity != null) {
            this.issuedQuantity++;
        }
    }

}