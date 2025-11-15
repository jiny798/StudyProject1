package com.sparta.camp.java.FinalProject.domain.user.repository;

import com.sparta.camp.java.FinalProject.domain.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}