package com.sparta.camp.java.FinalProject.domain.coupon.service;

import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.response.UserCouponResponse;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponCalculateService calculateService;

    @Transactional
    public BigDecimal applyDiscountAndMarkAsUsed(Long userId, Long userCouponId, BigDecimal originalTotalPrice) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));
        UserCoupon userCoupon = userCouponRepository.findByIdAndUser(userCouponId, user)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_COUPON));

        validateIsUsable(userCoupon, userId);

        Coupon coupon = userCoupon.getCoupon();
        BigDecimal discountedPrice = calculateService.calculateFinalPrice(coupon, originalTotalPrice);

        userCoupon.use();

        return discountedPrice;
    }


    private void validateIsUsable(UserCoupon userCoupon, Long userId) {
        // 보유 권한 체크
        if (!userCoupon.getUser().getId().equals(userId)) {
            throw new ServiceException(ServiceExceptionCode.COUPON_OWNERSHIP_MISMATCH);
        }

        // 사용 유무 체크
        if (userCoupon.isUsed()) {
            throw new ServiceException(ServiceExceptionCode.ALREADY_USED_COUPON);
        }

        // 만료일 체크
        if (LocalDateTime.now().isAfter(userCoupon.getExpirationDate())) {
            throw new ServiceException(ServiceExceptionCode.EXPIRED_COUPON);
        }

        userCoupon.use();
    }

    @Transactional(readOnly = true)
    public List<UserCouponResponse> getUserCoupons(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ServiceException(ServiceExceptionCode.NOT_FOUND_USER);
        }

        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);

        return userCoupons.stream()
                .map(
                        userCoupon -> new UserCouponResponse(
                                userCoupon.getId(),
                                userCoupon.getCoupon().getName(),
                                userCoupon.getCoupon().getDescription(),
                                userCoupon.getExpirationDate(),
                                userCoupon.isUsed()
                        ))
                .collect(Collectors.toList());
    }
}
