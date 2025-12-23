package com.sparta.camp.java.FinalProject.domain.order.service;

import com.sparta.camp.java.FinalProject.common.enums.OrderStatus;
import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.response.AdminOrderSummaryResponse;
import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import com.sparta.camp.java.FinalProject.domain.order.repository.OrderRepository;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestPage;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.PagingResponse;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 사용 (성능 향상)
public class AdminOrderService {

    private final OrderRepository orderRepository;

    /**
     * 주문 목록 조회
     */
    public PagingResponse<AdminOrderSummaryResponse> getOrderList(RequestPage requestPage) {
        Page<Order> orderPage = orderRepository.findAllWithUser(requestPage.getPageable());
//        Page<AdminOrderSummaryResponse> orderSummaryPage = orderPage.map(AdminOrderSummaryResponse::from);
        return new PagingResponse<>(orderPage, AdminOrderSummaryResponse.class);
    }

    public AdminOrderSummaryResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_ORDER)); // 예외 코드는 프로젝트에 맞게 수정
        return AdminOrderSummaryResponse.from(order);
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