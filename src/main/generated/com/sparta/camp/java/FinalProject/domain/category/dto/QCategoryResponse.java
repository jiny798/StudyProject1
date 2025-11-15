package com.sparta.camp.java.FinalProject.domain.category.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.sparta.camp.java.FinalProject.domain.category.dto.QCategoryResponse is a Querydsl Projection type for CategoryResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QCategoryResponse extends ConstructorExpression<CategoryResponse> {

    private static final long serialVersionUID = 140215482L;

    public QCategoryResponse(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> name) {
        super(CategoryResponse.class, new Class<?>[]{long.class, String.class}, id, name);
    }

}

