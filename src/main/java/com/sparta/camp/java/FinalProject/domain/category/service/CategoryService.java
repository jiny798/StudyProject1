package com.sparta.camp.java.FinalProject.domain.category.service;

import com.sparta.camp.java.FinalProject.domain.category.dto.CategoryResponse;
import com.sparta.camp.java.FinalProject.domain.category.entity.Category;
import com.sparta.camp.java.FinalProject.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getCategoryTree() {
        List<Category> categories = categoryRepository.findAll();
        Map<Long, CategoryResponse> responseMap = new HashMap<>();
        List<CategoryResponse> rootCategories = new ArrayList<>();

        for (Category category : categories) {
            responseMap.put(category.getId(), new CategoryResponse(category));
        }

        for (Category childCategory : categories) {
            CategoryResponse categoryResponse = responseMap.get(childCategory.getId());

            if (childCategory.getParent() != null) {
                Long parentId = childCategory.getParent().getId();
                CategoryResponse parentResponse = responseMap.get(parentId);
                parentResponse.getChildren().add(categoryResponse);
            } else {
                rootCategories.add(categoryResponse);
            }
        }
        return rootCategories;
    }

}