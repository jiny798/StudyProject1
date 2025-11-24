package com.sparta.camp.java.FinalProject.domain.product.controller.dto.response;

import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import com.sparta.camp.java.FinalProject.domain.product.entity.ProductImages;
import com.sparta.camp.java.FinalProject.domain.product.entity.ProductOption;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ProductResponse {

    private final Long id;
    private final String name;
    private final BigDecimal price;
    private Map<String, Object> options = new HashMap<>();
    private final String description;
    private final List<String> productImages;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.productImages = product.getProductImages().stream()
                .map(ProductImages::getImageUrl).toList();
    }

}
