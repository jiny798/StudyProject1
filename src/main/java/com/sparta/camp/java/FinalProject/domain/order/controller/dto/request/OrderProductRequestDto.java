package com.sparta.camp.java.FinalProject.domain.order.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductRequestDto {

    @NotNull(message = "상품 ID는 필수입니다.")
    Long productId;

    @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
    Integer quantity = 1; // 기본값 1

    private Long productOptionId;

}