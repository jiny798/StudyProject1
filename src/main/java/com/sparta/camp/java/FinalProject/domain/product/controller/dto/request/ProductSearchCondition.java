package com.sparta.camp.java.FinalProject.domain.product.controller.dto.request;

import com.sparta.camp.java.FinalProject.common.page.RequestPage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCondition extends RequestPage {
    private Long categoryId;
}
