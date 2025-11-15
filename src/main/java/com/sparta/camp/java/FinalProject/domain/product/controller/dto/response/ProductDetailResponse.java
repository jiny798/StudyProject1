package com.sparta.camp.java.FinalProject.domain.product.controller.dto.response;

import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import com.sparta.camp.java.FinalProject.domain.product.entity.ProductImages;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class ProductDetailResponse {
    private final Long id;
    private final String name;
    private final BigDecimal price;
    private Map<String, Object> options = new HashMap<>();
    private final String description;
    private final List<String> productImages;

    public ProductDetailResponse(Product product, Map<String, Object> productOption) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.options = productOption;
        this.description = product.getDescription();
        this.productImages = product.getProductImages().stream().map(ProductImages::getImageUrl).collect(Collectors.toList());
    }
}
