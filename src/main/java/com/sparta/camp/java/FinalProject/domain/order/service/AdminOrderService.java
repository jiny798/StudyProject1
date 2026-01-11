package com.sparta.camp.java.FinalProject.domain.order.service;

import com.sparta.camp.java.FinalProject.common.enums.OrderStatus;
import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.response.AdminOrderSummaryResponse;
import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import com.sparta.camp.java.FinalProject.domain.order.repository.OrderRepository;
import com.sparta.camp.java.FinalProject.common.page.RequestPage;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 사용 (성능 향상)
public class AdminOrderService {

    private final OrderRepository orderRepository;


    public PagingResponse<AdminOrderSummaryResponse> getOrderList(RequestPage requestPage) {
        Page<Order> orderPage = orderRepository.getList(requestPage, null);
        return new PagingResponse<>(orderPage, AdminOrderSummaryResponse.class);
    }

    public AdminOrderSummaryResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_ORDER)); // 예외 코드는 프로젝트에 맞게 수정
        return AdminOrderSummaryResponse.from(order);
    }

    @Transactional
    public void startDelivery(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_ORDER));
        order.setStatus(OrderStatus.DELIVERY);
    }

    @Transactional
    public void completeDelivery(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_ORDER));
        order.setStatus(OrderStatus.COMPLETED);
    }


    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_ORDER));

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new ServiceException(ServiceExceptionCode.ALREADY_CANCELED_ORDER);
        }

        order.setStatus(OrderStatus.CANCELED);
    }
}