package com.sparta.camp.java.FinalProject.domain.order.controller.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductResponse {
    private Long orderId;

    private List<OrderProductResponseDto> products;

    private BigDecimal totalPrice;

    private BigDecimal discountPrice;

}
