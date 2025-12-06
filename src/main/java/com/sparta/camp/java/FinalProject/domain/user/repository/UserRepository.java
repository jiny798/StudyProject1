package com.sparta.camp.java.FinalProject.domain.user.repository;

import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import com.sparta.camp.java.FinalProject.domain.user.repository.querydsl.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

  Optional<User> findByEmail(String email);

  List<User> findByCreatedAtAfterOrderByNameAsc(LocalDateTime dateTime);

  long countByName(String name);

  Optional<User> findUserByEmail(@Param("email") String email);

  @Query("SELECT u FROM User u JOIN FETCH u.orders")
  List<User> findAllWithOrders();

}