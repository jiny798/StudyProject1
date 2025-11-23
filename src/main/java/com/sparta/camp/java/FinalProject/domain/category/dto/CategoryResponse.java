package com.sparta.camp.java.FinalProject.domain.category.dto;

import com.sparta.camp.java.FinalProject.domain.category.entity.Category;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CategoryResponse {
    private final Long id;
    private final String name;
    private final Long parentId;
    private final List<CategoryResponse> children = new ArrayList<>();

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.parentId = (category.getParent() != null) ? category.getParent().getId() : null;
    }

}