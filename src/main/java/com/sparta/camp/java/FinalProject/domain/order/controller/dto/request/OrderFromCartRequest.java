package com.sparta.camp.java.FinalProject.domain.order.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderFromCartRequest {

    @NotNull
    private Long userId;

    @NotNull
    private String shippingAddress;

    private Long couponId;
}
