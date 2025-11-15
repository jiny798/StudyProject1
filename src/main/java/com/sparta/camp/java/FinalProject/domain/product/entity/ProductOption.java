package com.sparta.camp.java.FinalProject.domain.product.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "product_options")
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ProductOption parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<ProductOption> children = new ArrayList<>();

    @Column(name = "stock")
    private int stock;

    @Builder
    public ProductOption(Product product, String name, ProductOption parent, int stock) {
        this.name = name;
        this.stock = stock;
        this.product = product;
        if (parent != null) {
            this.setParent(parent);
        }
    }

    public void setParent(ProductOption parent) {
        if (this.parent != null) {
            this.parent.getChildren().remove(this);
        }
        this.parent = parent;
        if (parent != null) {
            parent.getChildren().add(this);
        }
    }


}