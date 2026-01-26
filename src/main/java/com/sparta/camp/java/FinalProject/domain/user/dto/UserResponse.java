package com.sparta.camp.java.FinalProject.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    Long id;

    String name;

    String email;

    String phone;

    LocalDateTime createdAt;

    @QueryProjection
    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.createdAt = user.getCreatedAt();
    }

}