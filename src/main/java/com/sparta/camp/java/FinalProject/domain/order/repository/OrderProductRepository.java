package com.sparta.camp.java.FinalProject.domain.order.repository;

import com.sparta.camp.java.FinalProject.domain.order.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

  List<OrderProduct> findByOrder_Id(Long purchaseId);

}