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
import com.sparta.camp.java.FinalProject.domain.product.entity.ProductOption;
import com.sparta.camp.java.FinalProject.domain.product.repository.ProductOptionRepository;
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
    private final ProductOptionRepository productOptionRepository;

    @Transactional
    public Order process(User user, List<OrderProductRequestDto> orderItems, String shoppingAddress, Long userCouponId) {
        Order order = createAndSavePurchase(user, shoppingAddress);
        List<OrderProduct> orderProducts = createAndProcessOrderProducts(
                orderItems,
                order);

        BigDecimal originalTotalPrice = calculateTotalPrice(orderProducts);
        BigDecimal finalTotalPrice = originalTotalPrice;

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (userCouponId != null) {
            finalTotalPrice = couponService.applyDiscountAndMarkAsUsed(
                    user.getId(),
                    userCouponId,
                    originalTotalPrice
            );
            discountAmount = originalTotalPrice.subtract(finalTotalPrice);
        }
        order.setTotalPrice(finalTotalPrice, discountAmount);

        return order;
    }

    private Order createAndSavePurchase(User user, String shoppingAddress) {
        return orderRepository.save(Order.builder()
                .user(user)
                .totalPrice(BigDecimal.ZERO)
                .discountedPrice(BigDecimal.ZERO)
                .status(OrderStatus.PENDING)
                .shippingAddress(shoppingAddress)
                .build());
    }

    private List<OrderProduct> createAndProcessOrderProducts(
            List<OrderProductRequestDto> itemRequests, Order order) {
        List<OrderProduct> purchaseProducts = new ArrayList<>();

        for (OrderProductRequestDto itemRequest : itemRequests) {
            Product product = productRepository.findById(itemRequest.getProductId()).orElseThrow();

            ProductOption finalOption = null;
            if (!itemRequest.getSelectedOptionNames().isEmpty()) {
                finalOption = findFinalOption(product, itemRequest.getSelectedOptionNames());
                finalOption.removeStock(itemRequest.getCount());
            }

            OrderProduct purchaseProduct = OrderProduct.builder()
                    .product(product)
                    .order(order)
                    .price(product.getPrice())
                    .count(itemRequest.getCount())
                    .build();

            purchaseProducts.add(purchaseProduct);
        }

        orderProductRepository.saveAll(purchaseProducts);
        return purchaseProducts;
    }

    private ProductOption findFinalOption(Product product, List<String> optionNames) {
        ProductOption currentParent = null;

        for (String optionName : optionNames) {
            ProductOption foundOption = productOptionRepository.findByProductAndNameAndParent(product, optionName, currentParent)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션: " + optionName));

            currentParent = foundOption;
        }

        return currentParent;
    }

    private BigDecimal calculateTotalPrice(List<OrderProduct> purchaseProducts) {
        return purchaseProducts.stream()
                .map(purchaseProduct -> purchaseProduct.getPrice()
                        .multiply(BigDecimal.valueOf(purchaseProduct.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}