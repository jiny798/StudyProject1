package com.sparta.camp.java.FinalProject.domain.order.service;

import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.cart.repository.CartRepository;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.*;
import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import com.sparta.camp.java.FinalProject.domain.order.entity.OrderProduct;
import com.sparta.camp.java.FinalProject.domain.order.repository.OrderRepository;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderProcessService purchaseProcessService;
    private final OrderCancelService purchaseCancelService;
    private final UserRepository userRepository;
    private final OrderRepository purchaseRepository;
    private final CartRepository cartRepository;

    @Transactional
    public OrderCompleteResponse order(OrderRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));

        Order order = purchaseProcessService.process(user, request.getProducts(), request.getShippingAddress(), request.getUserCouponId());

        return OrderCompleteResponse.builder()
                .orderId(order.getId())
                .totalPrice(order.getTotalPrice())
                .build();
    }

    public List<OrderProductResponse> getOrderList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));
        List<OrderProductResponse> response = new ArrayList<>();

        List<Order> orders = user.getOrders();
        orders.forEach(order -> {

            List<OrderProductResponseDto> orderProducts = order.getOrderProducts().stream()
                    .map(orderProduct -> {
                        return OrderProductResponseDto.builder()
                                .productId(orderProduct.getProduct().getId())
                                .productName(orderProduct.getProduct().getName())
                                .quantity(orderProduct.getQuantity())
                                .totalPrice(orderProduct.getPrice())
                                .build();
                    }).toList();

            OrderProductResponse orderProductResponse = OrderProductResponse.builder()
                    .orderId(order.getId())
                    .totalPrice(order.getTotalPrice())
                    .discountPrice(order.getDiscountedPrice())
                    .products(orderProducts)
                    .build();

            response.add(orderProductResponse);
        });

        return response;
    }

}
