package com.sparta.camp.java.FinalProject.domain.cart.repository;

import com.sparta.camp.java.FinalProject.domain.cart.entity.Cart;
import com.sparta.camp.java.FinalProject.domain.cart.entity.CartItem;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}