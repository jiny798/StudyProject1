package com.sparta.camp.java.FinalProject.domain.order.controller;

import com.sparta.camp.java.FinalProject.common.response.ApiResponse;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.request.OrderSearchCondition;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.response.AdminOrderSummaryResponse;
import com.sparta.camp.java.FinalProject.domain.order.service.AdminOrderService;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    private final AdminOrderService adminOrderService;

    @GetMapping
    public ApiResponse<PagingResponse<AdminOrderSummaryResponse>> getList(@ModelAttribute OrderSearchCondition searchCondition) {
        System.out.println("searchCondition " + searchCondition.getStatus());
        PagingResponse<AdminOrderSummaryResponse> response = adminOrderService.getOrderList(searchCondition);
        return ApiResponse.success(response);
    }

    @GetMapping("/{orderId}")
    public ApiResponse<AdminOrderSummaryResponse> getDetail(@PathVariable("orderId") Long orderId) {
        AdminOrderSummaryResponse response = adminOrderService.getOrderDetail(orderId);
        return ApiResponse.success(response);
    }

    @PostMapping("/{orderId}/start-delivery")
    public ApiResponse<Void> startDelivery(@PathVariable("orderId") Long orderId) {
        adminOrderService.startDelivery(orderId);
        return ApiResponse.success();
    }

    @PostMapping("/{orderId}/complete-delivery")
    public ApiResponse<Void> completeDelivery(@PathVariable("orderId") Long orderId) {
        adminOrderService.completeDelivery(orderId);
        return ApiResponse.success();
    }

    @PostMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(@PathVariable("orderId") Long orderId) {
        adminOrderService.cancelOrder(orderId);
        return ApiResponse.success();
    }
}
