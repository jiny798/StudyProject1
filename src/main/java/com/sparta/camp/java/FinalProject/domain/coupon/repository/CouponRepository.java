package com.sparta.camp.java.FinalProject.domain.coupon.repository;

import com.sparta.camp.java.FinalProject.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
