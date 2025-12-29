package com.sparta.camp.java.FinalProject.domain.coupon.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.Coupon;
import com.sparta.camp.java.FinalProject.common.page.RequestPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static com.sparta.camp.java.FinalProject.domain.coupon.entity.QCoupon.coupon;

@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Coupon> getList(RequestPage requestPage) {
        long totalCount = jpaQueryFactory.select(coupon.count())
                .from(coupon)
                .fetchFirst();

        List<Coupon> items = jpaQueryFactory.selectFrom(coupon)
                .limit(requestPage.getSize())
                .offset(requestPage.getOffset())
                .orderBy(coupon.id.desc())
                .fetch();

        return new PageImpl<>(items, requestPage.getPageable(), totalCount);
    }
}
