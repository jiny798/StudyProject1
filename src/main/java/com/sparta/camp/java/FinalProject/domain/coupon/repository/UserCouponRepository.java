package com.sparta.camp.java.FinalProject.domain.coupon.repository;

import com.sparta.camp.java.FinalProject.domain.coupon.entity.UserCoupon;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findByUserId(Long userId);

    Optional<UserCoupon> findByIdAndUser(Long userId, User user);

    List<UserCoupon> findAllByUserIdAndIsUsedFalseAndExpirationDateAfter(Long userId, LocalDateTime now);
}
