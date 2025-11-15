package com.sparta.camp.java.FinalProject.domain.order.service;

import com.sparta.camp.java.FinalProject.common.enums.OrderStatus;
import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.coupon.service.CouponService;
import com.sparta.camp.java.FinalProject.domain.order.controller.dto.OrderProductRequestDto;
import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import com.sparta.camp.java.FinalProject.domain.order.entity.OrderProduct;
import com.sparta.camp.java.FinalProject.domain.order.repository.OrderProductRepository;
import com.sparta.camp.java.FinalProject.domain.order.repository.OrderRepository;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import com.sparta.camp.java.FinalProject.domain.product.repository.ProductRepository;
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
public class OrderProcessService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;
    private final CouponService couponService;

    @Transactional
    public Order process(User user, List<OrderProductRequestDto> orderItems, String shoppingAddress, Long userCouponId) {
        Order order = createAndSavePurchase(user, shoppingAddress);
        List<OrderProduct> orderroducts = createAndProcessOrderProducts(
                orderItems,
                order);

        BigDecimal originalTotalPrice = calculateTotalPrice(orderroducts);
        BigDecimal finalTotalPrice = originalTotalPrice;

        if (userCouponId != null) {
            finalTotalPrice = couponService.applyDiscountAndMarkAsUsed(
                    user.getId(),
                    userCouponId,
                    originalTotalPrice
            );
        }
        order.setTotalPrice(finalTotalPrice);

        return order;
    }

    private Order createAndSavePurchase(User user, String shoppingAddress) {
        return orderRepository.save(Order.builder()
                .user(user)
                .totalPrice(BigDecimal.ZERO)
                .status(OrderStatus.PENDING)
                .shippingAddress(shoppingAddress)
                .build());
    }

    private List<OrderProduct> createAndProcessOrderProducts(
            List<OrderProductRequestDto> itemRequests, Order order) {
        List<OrderProduct> purchaseProducts = new ArrayList<>();

        for (OrderProductRequestDto itemRequest : itemRequests) {
            Product product = productRepository.findById(itemRequest.getProductId()).orElseThrow();

            validateStock(product, itemRequest.getQuantity());
            product.reduceStock(itemRequest.getQuantity());

            OrderProduct purchaseProduct = OrderProduct.builder()
                    .product(product)
                    .order(order)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice())
                    .build();

            purchaseProducts.add(purchaseProduct);
        }

        orderProductRepository.saveAll(purchaseProducts);
        return purchaseProducts;
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (requestedQuantity > product.getStock()) {
            throw new ServiceException(ServiceExceptionCode.OUT_OF_STOCK_PRODUCT);
        }
    }

    private BigDecimal calculateTotalPrice(List<OrderProduct> purchaseProducts) {
        return purchaseProducts.stream()
                .map(purchaseProduct -> purchaseProduct.getPrice()
                        .multiply(BigDecimal.valueOf(purchaseProduct.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}