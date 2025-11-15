package com.sparta.camp.java.FinalProject.domain.order.controller.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCompleteResponse {
    private Long orderId;
    private BigDecimal totalPrice;
}
