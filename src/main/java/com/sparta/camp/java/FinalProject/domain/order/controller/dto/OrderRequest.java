package com.sparta.camp.java.FinalProject.domain.order.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {

    @NotNull
    List<OrderProductRequestDto> products;

    @NotNull
    private String shippingAddress;

    private Long userCouponId;

}