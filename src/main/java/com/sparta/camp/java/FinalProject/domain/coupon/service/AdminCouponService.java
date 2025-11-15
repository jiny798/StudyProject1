package com.sparta.camp.java.FinalProject.domain.coupon.service;

import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.request.CreateCouponRequest;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.response.CouponResponse;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.Coupon;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.UserCoupon;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.type.ExpirationType;
import com.sparta.camp.java.FinalProject.domain.coupon.repository.CouponRepository;
import com.sparta.camp.java.FinalProject.domain.coupon.repository.UserCouponRepository;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminCouponService {
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    @Transactional
    public CouponResponse createCoupon(CreateCouponRequest request) {
        Coupon coupon = request.toEntity();
        coupon.validateTemplate();
        coupon.activate();

        Coupon savedCoupon = couponRepository.save(coupon);

        return CouponResponse.from(savedCoupon);

    }

    @Transactional
    public void deleteCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_COUPON));
        coupon.deactivate();

    }

    public void issueCouponToUser(Long userId, Long couponId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_COUPON));

        LocalDateTime expirationDate = calculateExpirationDate(coupon);

        UserCoupon userCoupon = UserCoupon.builder()
                .user(user)
                .coupon(coupon)
                .isUsed(false)
                .expirationDate(expirationDate)
                .build();

        userCouponRepository.save(userCoupon);

    }

    private LocalDateTime calculateExpirationDate(Coupon coupon) {
        if (ExpirationType.FIXED_PERIOD.equals(coupon.getExpirationType())) {
            return LocalDateTime.now().plusDays(coupon.getValidDays());
        } else if (ExpirationType.DATE_RANGE.equals(coupon.getExpirationType())) {
            return coupon.getEndDate();
        }
        throw new IllegalArgumentException("유효하지 않은 쿠폰 만료 타입입니다.");

    }
}
