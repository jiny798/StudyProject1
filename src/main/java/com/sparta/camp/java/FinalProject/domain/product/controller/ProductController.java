package com.sparta.camp.java.FinalProject.domain.product.controller;

import com.sparta.camp.java.FinalProject.common.response.ApiResponse;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestPage;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.PagingResponse;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.ProductDetailResponse;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.ProductResponse;
import com.sparta.camp.java.FinalProject.domain.product.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final AdminProductService adminProductService;

    @GetMapping("/{productId}")
    public ApiResponse<ProductDetailResponse> get(@PathVariable("productId") Long productId) {
        return ApiResponse.success(adminProductService.get(productId));
    }

    @GetMapping("/products")
    public ApiResponse<PagingResponse<ProductResponse>> getList(@ModelAttribute RequestPage requestPage) throws Exception {
        return ApiResponse.success(adminProductService.getList(requestPage));
    }

}