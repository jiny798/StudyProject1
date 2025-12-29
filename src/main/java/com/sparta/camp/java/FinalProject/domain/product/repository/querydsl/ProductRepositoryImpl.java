package com.sparta.camp.java.FinalProject.domain.product.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.camp.java.FinalProject.common.page.RequestPage;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.ProductSearchCondition;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static com.sparta.camp.java.FinalProject.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Product> getList(RequestPage requestPage) {
        long totalCount = jpaQueryFactory.select(product.count())
                .from(product)
                .fetchFirst();

        List<Product> items = jpaQueryFactory.selectFrom(product)
                .where(product.category.id.eq(((ProductSearchCondition) requestPage).getCategoryId()))
                .limit(requestPage.getSize())
                .offset(requestPage.getOffset())
                .orderBy(product.id.desc())
                .fetch();

        return new PageImpl<>(items, requestPage.getPageable(), totalCount);
    }
}
