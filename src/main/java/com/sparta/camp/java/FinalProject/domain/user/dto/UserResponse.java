package com.sparta.camp.java.FinalProject.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
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

    LocalDateTime createdAt;

    @QueryProjection
    public UserResponse(
            Long id,
            String name,
            String email,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }
}