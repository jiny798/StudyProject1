package com.sparta.camp.java.FinalProject.domain.coupon.entity;

import com.google.gson.stream.JsonWriter;
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
    }

    public void activate() {
        this.status = CouponStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = CouponStatus.INACTIVE;
    }

    public void validateTemplate() {
        if (this.expirationType == ExpirationType.DATE_RANGE &&
                (this.startDate == null || this.endDate == null || this.startDate.isAfter(this.endDate))) {
            throw new IllegalArgumentException("기간 설정이 올바르지 않습니다.");
        }

        if (this.expirationType == ExpirationType.FIXED_PERIOD && this.validDays == null) {
            throw new IllegalArgumentException("쿠폰 유효 일이 설정되지 않았습니다.");
        }
    }

}