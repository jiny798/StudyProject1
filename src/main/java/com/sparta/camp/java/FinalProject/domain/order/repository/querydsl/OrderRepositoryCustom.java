package com.sparta.camp.java.FinalProject.domain.order.repository.querydsl;

import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import com.sparta.camp.java.FinalProject.common.page.RequestPage;
import org.springframework.data.domain.Page;

public interface OrderRepositoryCustom {

    Page<Order> getList(RequestPage requestPage);
}