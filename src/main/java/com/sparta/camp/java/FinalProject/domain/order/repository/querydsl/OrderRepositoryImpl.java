package com.sparta.camp.java.FinalProject.domain.order.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.camp.java.FinalProject.common.enums.OrderStatus;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.request.OrderSearchCondition;
import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import com.sparta.camp.java.FinalProject.common.page.RequestPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.sparta.camp.java.FinalProject.domain.order.entity.QOrder.order;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Order> getList(RequestPage requestPage, Long userId) {
        OrderSearchCondition condition = (requestPage instanceof OrderSearchCondition)
                ? (OrderSearchCondition) requestPage
                : new OrderSearchCondition(); // 조건이 없으면 빈 객체로 처리

        List<Order> items = jpaQueryFactory.selectFrom(order)
                .where(
                        eqStatus(condition.getStatus()),         // 상태 일치 여부
                        goeStartDate(condition.getStartDate()),  // 시작일 이상 (>=)
                        loeEndDate(condition.getEndDate()),       // 종료일 이하 (<=)
                        eqUser(userId)
                )
                .limit(requestPage.getSize())
                .offset(requestPage.getOffset())
                .orderBy(order.id.desc())
                .fetch();

        Long totalCount = jpaQueryFactory.select(order.count())
                .from(order)
                .where(
                        eqStatus(condition.getStatus()),
                        goeStartDate(condition.getStartDate()),
                        loeEndDate(condition.getEndDate())
                )
                .fetchOne();

        if (totalCount == null) totalCount = 0L;

        return new PageImpl<>(items, requestPage.getPageable(), totalCount);
    }

    public Long getOrderCount(Long userId) {
        return jpaQueryFactory.select(order.count())
                .from(order)
                .where(order.user.id.eq(userId))
                .fetchOne();
    }

    // 사용자 조건
    private BooleanExpression eqUser(Long userId) {
        return order.user.id.eq(userId);
    }

    // 상태 조건
    private BooleanExpression eqStatus(OrderStatus status) {
        return status != null ? order.status.eq(status) : null;
    }

    // 시작 날짜 조건 (createdAt >= startDate 00:00:00)
    private BooleanExpression goeStartDate(String startDate) {
        if (!StringUtils.hasText(startDate)) {
            return null;
        }
        // String("yyyy-MM-dd") -> LocalDate -> LocalDateTime(00:00:00)
        LocalDate date = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        return order.createdAt.goe(date.atStartOfDay());
    }

    // 종료 날짜 조건 (createdAt <= endDate 23:59:59.999999)
    private BooleanExpression loeEndDate(String endDate) {
        if (!StringUtils.hasText(endDate)) {
            return null;
        }
        // String("yyyy-MM-dd") -> LocalDate -> LocalDateTime(23:59:59)
        LocalDate date = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        return order.createdAt.loe(date.atTime(LocalTime.MAX));
    }
}
