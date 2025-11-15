package com.sparta.camp.java.FinalProject.domain.order.controller;

import com.sparta.camp.java.FinalProject.domain.order.controller.dto.OrderRequest;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.OrderCompleteResponse;
import com.sparta.camp.java.FinalProject.domain.order.service.OrderService;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderCompleteResponse> order(@AuthenticationPrincipal User user, @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.order(request, user.getId()));
    }

    @GetMapping
    public ResponseEntity<OrderCompleteResponse> getOrders(@AuthenticationPrincipal User user) {
        return null;
    }

}