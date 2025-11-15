package com.sparta.camp.java.FinalProject.domain.order.controller.dto;

import com.sparta.camp.java.FinalProject.common.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCancelResponse {

    private Long orderId;

    private OrderStatus status;

    private LocalDateTime cancelledAt;

    private List<OrderProductResponse> cancelledProducts;

    private String message;

}