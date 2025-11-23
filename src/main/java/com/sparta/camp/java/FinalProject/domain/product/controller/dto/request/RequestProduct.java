package com.sparta.camp.java.FinalProject.domain.product.controller.dto.request;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestProduct {

    @NotBlank(message = "최소 1자 이상 입력주세요")
    @Size(min = 1, max = 255, message = "최소 1자 이상 입력주세요")
    private String name;

    @NotNull
    @Positive
    private BigDecimal price;

    private Long categoryId;

    @NotNull
    @Lob
    private String description;

    private List<String> productImages;

    private Map<String, Object> options;

}