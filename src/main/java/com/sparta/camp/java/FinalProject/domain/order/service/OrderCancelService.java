package com.sparta.camp.java.FinalProject.domain.order.service;

import com.sparta.camp.java.FinalProject.common.constant.Constants;
import com.sparta.camp.java.FinalProject.common.enums.OrderStatus;
import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.OrderCancelResponse;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.OrderProductResponse;
import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import com.sparta.camp.java.FinalProject.domain.order.entity.OrderProduct;
import com.sparta.camp.java.FinalProject.domain.order.repository.OrderProductRepository;
import com.sparta.camp.java.FinalProject.domain.order.repository.OrderRepository;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import com.sparta.camp.java.FinalProject.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCancelService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderCancelResponse cancelOrder(Long orderId, Long userId) {
        // 1. 구매 정보 조회 및 권한 확인
        Order order = orderRepository.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_ORDER));

        // 2. 취소 가능 여부 확인 및 상태 변경
        validatePurchaseStatus(order);

        // 3. 구매 상품 목록 조회
        List<OrderProduct> purchaseProducts = orderProductRepository.findByOrder_Id(orderId);

        // 4. 재고 복원
        restoreProductStock(purchaseProducts);

        List<OrderProductResponse> PurchaseProductResponses = getOrderProductResponses(purchaseProducts);

        return OrderCancelResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .cancelledAt(order.getUpdatedAt())
                .cancelledProducts(PurchaseProductResponses)
                .message(Constants.PURCHASE_CANCEL_MESSAGE)
                .build();
    }

    private void restoreProductStock(List<OrderProduct> purchaseProducts) {
        for (OrderProduct purchaseProduct : purchaseProducts) {
            Product product = purchaseProduct.getProduct();

            // 재고 복원 (현재 재고 + 취소된 수량)
            product.increaseStock(purchaseProduct.getQuantity());

            productRepository.save(product);
        }
    }

    private void validatePurchaseStatus(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ServiceException(ServiceExceptionCode.CANCEL_FAIL);
        }
        order.setStatus(OrderStatus.CANCELED);
    }

    private List<OrderProductResponse> getOrderProductResponses(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map((orderProduct) -> {
                    Product product = orderProduct.getProduct();
                    BigDecimal totalPrice = orderProduct.getPrice()
                            .multiply(BigDecimal.valueOf(orderProduct.getQuantity()));

                    return OrderProductResponse.builder()
                            .orderId(product.getId())
                            .totalPrice(totalPrice)
                            .build();
                }).toList();
    }
}