package com.sparta.camp.java.FinalProject.domain.cart.repository;

import com.sparta.camp.java.FinalProject.domain.cart.entity.Cart;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.product WHERE c.user = :user")
    Optional<Cart> findByUser(@Param("user") User user);

}