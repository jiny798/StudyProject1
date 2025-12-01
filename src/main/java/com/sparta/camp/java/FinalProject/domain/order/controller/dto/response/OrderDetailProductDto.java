package com.sparta.camp.java.FinalProject.domain.order.controller.dto.response;

import com.sparta.camp.java.FinalProject.domain.order.entity.OrderProduct;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderDetailProductDto {
    private final Long productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal price; // 개당 가격

    @Builder
    public OrderDetailProductDto(Long productId, String productName, int quantity, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderDetailProductDto from(OrderProduct orderProduct) {
        return OrderDetailProductDto.builder()
                .productId(orderProduct.getProduct().getId())
                .productName(orderProduct.getProduct().getName())
                .quantity(orderProduct.getQuantity())
                .price(orderProduct.getPrice())
                .build();
    }
}