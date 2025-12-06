package com.sparta.camp.java.FinalProject.domain.product.support;

import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestProduct;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.ProductResponse;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-06T22:15:52+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Eclipse Adoptium)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductResponse toDto(Product product) {
        if ( product == null ) {
            return null;
        }

        Product product1 = null;

        product1 = product;

        ProductResponse productResponse = new ProductResponse( product1 );

        return productResponse;
    }

    @Override
    public Product toEntity(RequestProduct requestProduct) {
        if ( requestProduct == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.name( requestProduct.getName() );
        product.description( requestProduct.getDescription() );
        product.price( requestProduct.getPrice() );

        return product.build();
    }
}
