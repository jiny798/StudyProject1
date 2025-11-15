package com.sparta.camp.java.FinalProject.domain.user.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.sparta.camp.java.FinalProject.domain.user.dto.QUserResponse is a Querydsl Projection type for UserResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QUserResponse extends ConstructorExpression<UserResponse> {

    private static final long serialVersionUID = -696283430L;

    public QUserResponse(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<String> email, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdAt) {
        super(UserResponse.class, new Class<?>[]{long.class, String.class, String.class, java.time.LocalDateTime.class}, id, name, email, createdAt);
    }

}

