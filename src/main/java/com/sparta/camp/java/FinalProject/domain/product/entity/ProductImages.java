package com.sparta.camp.java.FinalProject.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Entity
@NoArgsConstructor
@Table(name = "product_images")
public class ProductImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Setter
    private String imageUrl;

    @Builder
    public ProductImages(Product product, String imageUrl) {
        this.product = product;
        this.imageUrl = imageUrl;
    }

}
