package com.sparta.camp.java.FinalProject.domain.order.controller.dto.response;

import com.sparta.camp.java.FinalProject.domain.order.entity.OrderProduct;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductResponseDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal totalPrice;

    public OrderProductResponseDto(OrderProduct orderProduct) {
        this.productId = orderProduct.getId();
        this.productName = orderProduct.getProduct().getName();
        this.quantity = orderProduct.getQuantity();
        this.totalPrice = orderProduct.getPrice();
    }
}