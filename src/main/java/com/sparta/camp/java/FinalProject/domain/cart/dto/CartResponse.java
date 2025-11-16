package com.sparta.camp.java.FinalProject.domain.cart.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class CartResponse {
    private List<CartItemResponse> cartItems;
    private BigDecimal totalPrice;

    public static CartResponse of(List<CartItemResponse> cartItems, BigDecimal totalPrice) {
        return CartResponse.builder()
                .cartItems(cartItems)
                .totalPrice(totalPrice)
                .build();
    }
}