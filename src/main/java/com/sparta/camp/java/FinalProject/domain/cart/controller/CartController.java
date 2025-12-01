package com.sparta.camp.java.FinalProject.domain.cart.controller;

import com.sparta.camp.java.FinalProject.common.response.ApiResponse;
import com.sparta.camp.java.FinalProject.domain.cart.controller.dto.CartItemAddRequest;
import com.sparta.camp.java.FinalProject.domain.cart.controller.dto.CartItemUpdateRequest;
import com.sparta.camp.java.FinalProject.domain.cart.controller.dto.CartResponse;
import com.sparta.camp.java.FinalProject.domain.cart.service.CartService;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ApiResponse<Void> addCartItem(@AuthenticationPrincipal User user, @Valid @RequestBody CartItemAddRequest request) {
        cartService.addCartItem(user.getId(), request);
        return ApiResponse.success();
    }

    @GetMapping
    public ApiResponse<CartResponse> getCart(@AuthenticationPrincipal User user) {
        CartResponse response = cartService.getCart(user.getId());
        return ApiResponse.success(response);
    }

    @PatchMapping("/items/{cartItemId}")
    public ApiResponse<Void> updateCartItemQuantity(@AuthenticationPrincipal User user,
                                                    @PathVariable Long cartItemId,
                                                    @Valid @RequestBody CartItemUpdateRequest request) {
        cartService.updateCartItemQuantity(user.getId(), cartItemId, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<Void> removeCartItem(@AuthenticationPrincipal User user, @PathVariable Long cartItemId) {
        cartService.removeCartItem(user.getId(), cartItemId);
        return ApiResponse.success(null);
    }
}