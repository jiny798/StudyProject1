package com.sparta.camp.java.FinalProject.domain.order.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CancelledProductDto {
    private final Long productId;
    private final BigDecimal refundedPrice;

    @Builder
    public CancelledProductDto(Long productId, BigDecimal refundedPrice) {
        this.productId = productId;
        this.refundedPrice = refundedPrice;
    }
}