package com.sparta.camp.java.FinalProject.domain.order.controller.dto.response;

import com.sparta.camp.java.FinalProject.common.enums.OrderStatus;
import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderSummaryResponse {
    private Long orderId;
    private String userEmail;
    private String userName;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;

    private List<AdminOrderProductDto> orderProducts;

    public AdminOrderSummaryResponse(Order order) {
        this.orderId = order.getId();
        this.userEmail = order.getUser().getEmail();
        this.userName = order.getUser().getName();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();

        this.orderProducts = order.getOrderProducts().stream()
                .map(AdminOrderProductDto::from)
                .collect(Collectors.toList());
    }

    public static AdminOrderSummaryResponse from(Order order) {
        AdminOrderSummaryResponse response = new AdminOrderSummaryResponse();
        response.orderId = order.getId();
        response.userEmail = order.getUser().getEmail();
        response.userName = order.getUser().getName();
        response.totalPrice = order.getTotalPrice();
        response.status = order.getStatus();
        response.createdAt = order.getCreatedAt();

        response.orderProducts = order.getOrderProducts().stream()
                .map(AdminOrderProductDto::from)
                .collect(Collectors.toList());

        return response;
    }
}