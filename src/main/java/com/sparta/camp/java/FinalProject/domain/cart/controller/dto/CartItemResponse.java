package com.sparta.camp.java.FinalProject.domain.cart.controller.dto;

import com.sparta.camp.java.FinalProject.domain.cart.entity.CartItem;
import com.sparta.camp.java.FinalProject.domain.product.entity.ProductOption;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class CartItemResponse {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private String optionDescription;

    public static CartItemResponse from(CartItem cartItem) {
        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .quantity(cartItem.getQuantity())
                .optionDescription(createOptionString(cartItem.getProductOption()))
                .price(cartItem.getProduct().getPrice())
                .build();
    }

    private static String createOptionString(ProductOption option) {
        if (option == null) {
            return "";
        }

        // 1. 부모를 타고 올라가며 모든 이름 수집 (예: ["L", "Size", "그레이", "색상"])
        List<String> optionNames = new ArrayList<>();
        ProductOption current = option;
        while (current != null) {
            optionNames.add(current.getName());
            current = current.getParent();
        }

        // 2. 최상위 부모가 앞에 오도록 뒤집기 (예: ["색상", "그레이", "Size", "L"])
        Collections.reverse(optionNames);

        // 3. 두 개씩 짝지어서 문자열 만들기
        // 리스트에 이름이 ["Key1", "Value1", "Key2", "Value2"] 순서로 들어있다고 가정
        List<String> pairs = new ArrayList<>();
        for (int i = 0; i < optionNames.size(); i += 2) {
            String key = optionNames.get(i);
            // 값이 없는 홀수 개일 경우를 대비해 체크 (마지막 뎁스가 값 없이 끝나는 경우 등)
            String value = (i + 1 < optionNames.size()) ? optionNames.get(i + 1) : "";

            if (!value.isEmpty()) {
                pairs.add(key + ": " + value);
            } else {
                pairs.add(key); // 값 부분이 없으면 키만 출력
            }
        }

        // 4. " / " 로 연결하여 반환
        return String.join(" / ", pairs);
    }
}