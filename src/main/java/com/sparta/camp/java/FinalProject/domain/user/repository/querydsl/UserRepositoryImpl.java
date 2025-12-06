package com.sparta.camp.java.FinalProject.domain.user.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestPage;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import com.sparta.camp.java.FinalProject.domain.user.entity.QUser;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static com.sparta.camp.java.FinalProject.domain.product.entity.QProduct.product;
import static com.sparta.camp.java.FinalProject.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<User> getList(RequestPage requestPage) {
        long totalCount = jpaQueryFactory.select(user.count())
                .from(user)
                .fetchFirst();

        List<User> items = jpaQueryFactory.selectFrom(user)
                .limit(requestPage.getSize())
                .offset(requestPage.getOffset())
                .orderBy(user.id.desc())
                .fetch();

        return new PageImpl<>(items, requestPage.getPageable(), totalCount);
    }
}
