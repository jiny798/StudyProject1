package com.sparta.camp.java.FinalProject.domain.coupon.service;

import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.response.MyCouponResponse;
import com.sparta.camp.java.FinalProject.domain.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    public List<MyCouponResponse> getMyAvailableCoupons(Long userId) {
        return userCouponRepository
                .findAllByUserIdAndIsUsedFalseAndExpirationDateAfter(userId, LocalDateTime.now())
                .stream()
                .map(MyCouponResponse::from)
                .toList();
    }
}