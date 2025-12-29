package com.sparta.camp.java.FinalProject.domain.order.controller;

import com.sparta.camp.java.FinalProject.common.response.ApiResponse;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.request.OrderSearchCondition;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.response.OrderCancelResponse;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.response.OrderDetailResponse;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.response.OrderProductResponse;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.request.OrderRequest;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.response.OrderCompleteResponse;
import com.sparta.camp.java.FinalProject.domain.order.service.OrderService;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderCompleteResponse> order(@AuthenticationPrincipal User user, @Valid @RequestBody OrderRequest request) {
        System.out.println("user.getId() " + user.getId());
        OrderCompleteResponse response = orderService.order(request, user.getId());
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<OrderProductResponse>> getOrders(@AuthenticationPrincipal User user, @ModelAttribute OrderSearchCondition searchCondition) {
        System.out.println("user.getId() " + user.getId());
        List<OrderProductResponse> response = orderService.getOrderList(user.getId(), searchCondition);
        return ApiResponse.success(response);
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponse> getOrderDetail(@AuthenticationPrincipal User user, @PathVariable("orderId") Long orderId) {
        System.out.println("user.getId() " + user.getId() + ", orderId: " + orderId);
        OrderDetailResponse response = orderService.getOrderDetail(user.getId(), orderId);
        return ApiResponse.success(response);
    }

    @PostMapping("/{orderId}/cancel")
    public ApiResponse<OrderCancelResponse> cancelOrder(@AuthenticationPrincipal User user, @PathVariable("orderId") Long orderId) {
        System.out.println("user.getId() " + user.getId() + ", orderId: " + orderId);
        OrderCancelResponse response = orderService.cancelOrder(user.getId(), orderId);
        return ApiResponse.success(response);
    }
}