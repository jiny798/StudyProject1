package com.sparta.camp.java.FinalProject.domain.order.repository;

import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  Optional<Order> findByIdAndUser_Id(Long id, Long userId);

  @Query("SELECT o FROM Order o JOIN FETCH o.orderProducts op JOIN FETCH op.product WHERE o.id = :orderId AND o.user.id = :userId")
  Optional<Order> findOrderDetailsByIdAndUserId(@Param("orderId") Long orderId, @Param("userId") Long userId);

  @Query(value = "SELECT o FROM Order o JOIN FETCH o.user",
          countQuery = "SELECT COUNT(o) FROM Order o")
  Page<Order> findAllWithUser(Pageable pageable);
}