package com.sparta.camp.java.FinalProject.domain.coupon.service;

import com.sparta.camp.java.FinalProject.domain.coupon.entity.Coupon;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CouponCalculateService {
    public BigDecimal calculateFinalPrice(Coupon coupon, BigDecimal price) { // 이름 변경
        BigDecimal finalPrice = switch (coupon.getDiscountType()) {
            case PERCENTAGE -> {
                BigDecimal rate = coupon.getDiscountValue()
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                BigDecimal discountAmount = price.multiply(rate)
                        .setScale(0, RoundingMode.HALF_UP); // 할인액 반올림

                yield price.subtract(discountAmount);
            }
            case FIXED ->
                     price.subtract(coupon.getDiscountValue());

            default -> throw new IllegalArgumentException("지원하지 않는 할인 타입입니다.");
        };

        return finalPrice.max(BigDecimal.ZERO);
    }
}
