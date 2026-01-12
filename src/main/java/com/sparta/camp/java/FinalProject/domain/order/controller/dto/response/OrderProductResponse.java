package com.sparta.camp.java.FinalProject.domain.order.controller.dto.response;

import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductResponse {
    private Long orderId;

    private List<OrderProductResponseDto> products;

    private BigDecimal totalPrice;

    private BigDecimal discountPrice;

    public OrderProductResponse(Order order) {
        this.orderId = order.getId();
        totalPrice = order.getTotalPrice();
        products = order.getOrderProducts().stream().map(OrderProductResponseDto::new).toList();
        discountPrice = order.getDiscountedPrice();
    }
}
