package com.sparta.camp.java.FinalProject.domain.coupon.service;

import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.request.CreateCouponRequest;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.response.CouponResponse;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.response.CouponResponseDto;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.Coupon;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.UserCoupon;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.type.ExpirationType;
import com.sparta.camp.java.FinalProject.domain.coupon.repository.CouponRepository;
import com.sparta.camp.java.FinalProject.domain.coupon.repository.UserCouponRepository;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestPage;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.PagingResponse;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.ProductResponse;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    public PagingResponse<CouponResponseDto> getCouponList(RequestPage requestPage) {
        Page<Coupon> couponPage = couponRepository.getList(requestPage);
        return new PagingResponse<>(couponPage, CouponResponseDto.class);
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

        coupon.validateIssuable();

        LocalDateTime expirationDate = calculateExpirationDate(coupon);

        UserCoupon userCoupon = UserCoupon.builder()
                .user(user)
                .coupon(coupon)
                .isUsed(false)
                .expirationDate(expirationDate)
                .build();

        coupon.decreaseQuantity();

        userCouponRepository.save(userCoupon);

    }

    private LocalDateTime calculateExpirationDate(Coupon coupon) {
        if (ExpirationType.VALID_DAYS_ON_ISSUE.equals(coupon.getExpirationType())) {
            return LocalDateTime.now().plusDays(coupon.getValidDays());
        } else if (ExpirationType.DATE_RANGE.equals(coupon.getExpirationType())) {
            return coupon.getEndDate();
        }
        throw new ServiceException(ServiceExceptionCode.INVALID_COUPON_EXPIRATION_TYPE);
    }
}
