package com.sparta.camp.java.FinalProject.domain.category.controller;

import com.sparta.camp.java.FinalProject.common.response.ApiResponse;
import com.sparta.camp.java.FinalProject.domain.category.dto.CategoryRequest;
import com.sparta.camp.java.FinalProject.domain.category.dto.CategoryResponse;
import com.sparta.camp.java.FinalProject.domain.category.service.AdminCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponse>> findAll() {
        return ApiResponse.success(adminCategoryService.findAll());
    }

    @GetMapping("/leaves")
    public ApiResponse<List<CategoryResponse>> getLeafCategories() {
        return ApiResponse.success(adminCategoryService.getLeafCategories());
    }

    @PostMapping
    public ApiResponse<Void> save(@Valid @RequestBody CategoryRequest request) {
        adminCategoryService.save(request);
        return ApiResponse.success();
    }

    @PatchMapping("{categoryId}")
    public ApiResponse<Void> edit(@PathVariable("categoryId") Long categoryId, @Valid @RequestBody CategoryRequest request) {
        adminCategoryService.edit(categoryId, request);
        return ApiResponse.success();
    }

    @DeleteMapping("{categoryId}")
    public ApiResponse<Void> delete(@PathVariable("categoryId") Long categoryId) {
        adminCategoryService.delete(categoryId);
        return ApiResponse.success();
    }

}
