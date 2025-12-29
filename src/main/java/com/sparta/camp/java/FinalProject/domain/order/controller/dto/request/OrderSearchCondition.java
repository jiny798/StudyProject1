package com.sparta.camp.java.FinalProject.domain.order.controller.dto.request;

import com.sparta.camp.java.FinalProject.common.enums.OrderStatus;
import com.sparta.camp.java.FinalProject.common.page.RequestPage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchCondition extends RequestPage {
    private OrderStatus status;
    private String startDate;
    private String endDate;
}