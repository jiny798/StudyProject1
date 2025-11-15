package com.sparta.camp.java.FinalProject.domain.order.controller.dto.response;

import com.sparta.camp.java.FinalProject.common.enums.OrderStatus;
 import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
 import lombok.Builder;
 import lombok.Getter;
 
 import java.math.BigDecimal;
 import java.time.LocalDateTime;
 import java.util.List;
 
 @Getter
 public class OrderDetailResponse {
     private final Long orderId;
     private final BigDecimal totalPrice;
     private final BigDecimal discountedPrice;
     private final String shippingAddress;
     private final OrderStatus status;
     private final LocalDateTime orderDate;
     private final List<OrderDetailProductDto> products;
 
     @Builder
     public OrderDetailResponse(Long orderId, BigDecimal totalPrice, BigDecimal discountedPrice, String shippingAddress, OrderStatus status, LocalDateTime orderDate, List<OrderDetailProductDto> products) {
         this.orderId = orderId;
         this.totalPrice = totalPrice;
         this.discountedPrice = discountedPrice;
         this.shippingAddress = shippingAddress;
         this.status = status;
         this.orderDate = orderDate;
         this.products = products;
     }
 
     public static OrderDetailResponse from(Order order) {
         List<OrderDetailProductDto> productDtos = order.getOrderProducts().stream()
                 .map(OrderDetailProductDto::from)
                 .toList();
 
         return OrderDetailResponse.builder()
                 .orderId(order.getId())
                 .totalPrice(order.getTotalPrice())
                 .discountedPrice(order.getDiscountedPrice())
                 .shippingAddress(order.getShippingAddress())
                 .status(order.getStatus())
                 .orderDate(order.getCreatedAt())
                 .products(productDtos)
                 .build();
     }
 }