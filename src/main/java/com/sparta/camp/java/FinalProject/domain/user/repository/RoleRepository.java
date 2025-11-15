package com.sparta.camp.java.FinalProject.domain.user.repository;

import com.sparta.camp.java.FinalProject.domain.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String name);
}