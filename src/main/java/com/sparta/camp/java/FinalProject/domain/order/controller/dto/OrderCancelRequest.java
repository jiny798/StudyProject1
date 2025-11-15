package com.sparta.camp.java.FinalProject.domain.order.controller.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCancelRequest {

    Long orderId;

    Long userId;

}