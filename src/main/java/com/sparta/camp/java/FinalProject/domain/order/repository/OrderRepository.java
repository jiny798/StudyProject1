package com.sparta.camp.java.FinalProject.domain.order.repository;

import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  Optional<Order> findByIdAndUser_Id(Long id, Long userId);

}