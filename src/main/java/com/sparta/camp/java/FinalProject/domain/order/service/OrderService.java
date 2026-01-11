package com.sparta.camp.java.FinalProject.domain.order.service;

import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.request.OrderRequest;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.request.OrderSearchCondition;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.response.*;
import com.sparta.camp.java.FinalProject.domain.cart.repository.CartRepository;
import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import com.sparta.camp.java.FinalProject.domain.order.repository.OrderRepository;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.PagingResponse;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderProcessService purchaseProcessService;
    private final OrderCancelService purchaseCancelService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
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

    @Transactional
    public PagingResponse<OrderProductResponse> getOrderList(Long userId, OrderSearchCondition searchCondition) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));
        Page<Order> orders = orderRepository.getList(searchCondition, userId);

        return new PagingResponse<>(orders, OrderProductResponse.class);


//        return orders.map(order -> {
//            //내부 상품 리스트 변환 (Entity -> DTO)
//            List<OrderProductResponseDto> orderProducts = order.getOrderProducts().stream()
//                    .map(orderProduct -> OrderProductResponseDto.builder()
//                            .productId(orderProduct.getProduct().getId())
//                            .productName(orderProduct.getProduct().getName())
//                            .quantity(orderProduct.getQuantity())
//                            .totalPrice(orderProduct.getPrice())
//                            .build())
//                    .toList();
//
//            return OrderProductResponse.builder()
//                    .orderId(order.getId())
//                    .totalPrice(order.getTotalPrice())
//                    .discountPrice(order.getDiscountedPrice())
//                    // .orderDate(order.getCreatedAt()) // 앞선 Vue 코드에서 날짜가 필요하다면 추가
//                    .products(orderProducts)
//                    .build();
//        });
    }


    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(Long userId, Long orderId) {
        Order order = orderRepository.findOrderDetailsByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_ORDER));

        return OrderDetailResponse.from(order);
    }

    @Transactional
    public OrderCancelResponse cancelOrder(Long userId, Long orderId) {
        return purchaseCancelService.cancelOrder(orderId, userId);
    }

    public UserOrderCount getOrderCount(Long userId) {
        Long orderCount = orderRepository.getOrderCount(userId);
        if (orderCount == null) {
            orderCount = 0L;
        }
        return UserOrderCount.builder()
                .orderCount(orderCount.intValue())
                .build();
    }
}
