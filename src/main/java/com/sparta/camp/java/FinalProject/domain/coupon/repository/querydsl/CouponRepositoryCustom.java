package com.sparta.camp.java.FinalProject.domain.coupon.repository.querydsl;

import com.sparta.camp.java.FinalProject.domain.coupon.entity.Coupon;
import com.sparta.camp.java.FinalProject.common.page.RequestPage;
import org.springframework.data.domain.Page;

public interface CouponRepositoryCustom {

    Page<Coupon> getList(RequestPage requestPage);
}
