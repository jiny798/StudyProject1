package com.sparta.camp.java.FinalProject.domain.product.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
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
        BooleanExpression categoryCondition = eqCategoryId(requestPage);
        Long totalCount = jpaQueryFactory.select(product.count())
                .from(product)
                .where(categoryCondition) // 조건 적용
                .fetchOne();

        List<Product> items = jpaQueryFactory.selectFrom(product)
                .where(categoryCondition) // 조건 적용
                .limit(requestPage.getSize())
                .offset(requestPage.getOffset())
                .orderBy(product.id.desc())
                .fetch();

        if (totalCount == null) totalCount = 0L;

        return new PageImpl<>(items, requestPage.getPageable(), totalCount);
    }

    private BooleanExpression eqCategoryId(RequestPage requestPage) {
        if (requestPage instanceof ProductSearchCondition) {
            ProductSearchCondition condition = (ProductSearchCondition) requestPage;

            if (condition.getCategoryId() != null) {
                return product.category.id.eq(condition.getCategoryId());
            }
        }
        return null;
    }
}
