package com.sparta.camp.java.FinalProject.domain.product.controller;

import com.sparta.camp.java.FinalProject.common.response.ApiResponse;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestPage;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestProduct;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.PagingResponse;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.ProductDetailResponse;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.ProductResponse;
import com.sparta.camp.java.FinalProject.domain.product.service.ImageService;
import com.sparta.camp.java.FinalProject.domain.product.service.ProductService;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j

@RequestMapping("/api/admin")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminProductController {

    private final ProductService productService;
    private final ImageService imageService;
    private final String host = "http://localhost:8080";

    @PostMapping("/products")
    public ApiResponse<Void> post(@AuthenticationPrincipal User user, @RequestBody @Valid RequestProduct requestProduct) throws Exception {
        log.debug("ProductDto : {}", requestProduct);
        productService.createProduct(user.getId(), requestProduct);
        return ApiResponse.success();
    }

    @PostMapping("/images")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = imageService.upload(file);
        return ApiResponse.success(host + url);
    }

    @GetMapping("/products/{postId}")
    public ApiResponse<ProductDetailResponse> get(@PathVariable(name = "postId") Long postId) throws Exception {
        return ApiResponse.success(productService.get(postId));
    }

    @GetMapping("/products")
    public ApiResponse<PagingResponse<ProductResponse>> getList(@ModelAttribute RequestPage requestPage) throws Exception {
        return ApiResponse.success(productService.getList(requestPage));
    }


    @DeleteMapping("/products/{productId}")
    public ApiResponse<Void> delete(@PathVariable(name = "productId") Long productId) throws Exception {
        productService.delete(productId);
        return ApiResponse.success();
    }


}
