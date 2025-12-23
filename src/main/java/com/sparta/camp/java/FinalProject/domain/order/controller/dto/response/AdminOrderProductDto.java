package com.sparta.camp.java.FinalProject.domain.order.controller.dto.response;

import com.sparta.camp.java.FinalProject.domain.order.entity.OrderProduct;
import com.sparta.camp.java.FinalProject.domain.product.entity.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderProductDto {
    private String productName;
    private String optionName; // 옵션이 없으면 null or ""
    private int quantity;

    public static AdminOrderProductDto from(OrderProduct orderProduct) {
        AdminOrderProductDto dto = new AdminOrderProductDto();
        dto.productName = orderProduct.getProduct().getName();
        dto.quantity = orderProduct.getQuantity();

        // 옵션 처리 로직
        ProductOption option = orderProduct.getProductOption();
        if (option != null) {
            dto.optionName = option.getName(); 
        }
        return dto;
    }
}