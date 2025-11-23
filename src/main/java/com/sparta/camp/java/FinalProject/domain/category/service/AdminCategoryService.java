package com.sparta.camp.java.FinalProject.domain.category.service;

import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.category.dto.CategoryRequest;
import com.sparta.camp.java.FinalProject.domain.category.dto.CategoryResponse;
import com.sparta.camp.java.FinalProject.domain.category.entity.Category;
import com.sparta.camp.java.FinalProject.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getLeafCategories() {
        return categoryRepository.findByChildrenIsEmpty().stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long save(CategoryRequest request) {

        Category parentCategory = null;

        if (request.getParentId() != null) {
            parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CATEGORY));
        }

        Category category = categoryRepository.save(
                Category.builder()
                        .name(request.getName())
                        .parent(parentCategory)
                        .build()
        );


        return category.getId();
    }

    @Transactional
    public void edit(Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CATEGORY));

        Category parentCategory = null;

        if (request.getParentId() != null) {
            parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CATEGORY));

            if (Objects.equals(parentCategory.getId(), category.getId())) {
                new ServiceException(ServiceExceptionCode.CATEGORY_PARENT_CYCLE);
            }
        }

        category.setName(request.getName());
        category.setParent(parentCategory);

    }

    @Transactional
    public void delete(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CATEGORY));

        categoryRepository.delete(category);
    }

}
