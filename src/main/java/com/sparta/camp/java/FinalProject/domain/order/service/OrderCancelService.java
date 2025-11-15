package com.sparta.camp.java.FinalProject.domain.order.service;

import com.sparta.camp.java.FinalProject.common.constant.Constants;
import com.sparta.camp.java.FinalProject.common.enums.OrderStatus;
import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.response.CancelledProductDto;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.response.OrderCancelResponse;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.response.OrderProductResponse;
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
        Order order = orderRepository.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_ORDER));

        validatePurchaseStatus(order);

        List<OrderProduct> purchaseProducts = orderProductRepository.findByOrder_Id(orderId);
        restoreProductStock(purchaseProducts);

        List<CancelledProductDto> PurchaseProductResponses = getCancelledProductDtos(purchaseProducts);

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

    private List<CancelledProductDto> getCancelledProductDtos(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map((orderProduct) -> {
                    Product product = orderProduct.getProduct();
                    BigDecimal totalPrice = orderProduct.getPrice()
                            .multiply(BigDecimal.valueOf(orderProduct.getQuantity()));

                    return CancelledProductDto.builder()
                            .productId(product.getId())
                            .refundedPrice(totalPrice)
                            .build();
                }).toList();
    }
}