package com.sparta.camp.java.FinalProject.domain.product.controller;

import com.sparta.camp.java.FinalProject.common.response.ApiResponse;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestPage;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.PagingResponse;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.ProductDetailResponse;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.ProductResponse;
import com.sparta.camp.java.FinalProject.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ApiResponse<ProductDetailResponse> get(@PathVariable Long productId) {
        return ApiResponse.success(productService.get(productId));
    }

    @GetMapping("/products")
    public ApiResponse<PagingResponse<ProductResponse>> getList(@ModelAttribute RequestPage requestPage) throws Exception {
        return ApiResponse.success(productService.getList(requestPage));
    }

}