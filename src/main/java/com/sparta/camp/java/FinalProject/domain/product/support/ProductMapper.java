package com.sparta.camp.java.FinalProject.domain.product.support;

import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestProduct;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.ProductResponse;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "productImages", ignore = true)
    @Mapping(target = "options", ignore = true)
    ProductResponse toDto(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "productImages", ignore = true)
    Product toEntity(RequestProduct requestProduct);

}