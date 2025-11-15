package com.sparta.camp.java.FinalProject.domain.order.controller.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductResponseDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal totalPrice;

}