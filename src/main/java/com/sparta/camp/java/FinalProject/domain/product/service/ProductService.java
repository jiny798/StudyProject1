package com.sparta.camp.java.FinalProject.domain.product.service;


import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.category.entity.Category;
import com.sparta.camp.java.FinalProject.domain.category.repository.CategoryRepository;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestPage;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.ProductDetailResponse;
import com.sparta.camp.java.FinalProject.domain.product.entity.ProductImages;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestProduct;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.PagingResponse;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.ProductResponse;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import com.sparta.camp.java.FinalProject.domain.product.entity.ProductOption;
import com.sparta.camp.java.FinalProject.domain.product.repository.ProductOptionRepository;
import com.sparta.camp.java.FinalProject.domain.product.repository.ProductRepository;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductOptionRepository productOptionRepository;

    public void createProduct(Long userId, RequestProduct req) {
        var user = userRepository.findById(userId).orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));
        var category = categoryRepository.findById(req.getCategoryId()).orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CATEGORY));
        Product product = saveProduct(req, category, user);

        if (req.getOptions() != null && !req.getOptions().isEmpty()) {
            List<ProductOption> options = parseOptions(product, null, req.getOptions());
            productOptionRepository.saveAll(options);
        }
    }

    private Product saveProduct(RequestProduct requestProduct, Category category, User user) {
        Product product = Product.builder()
                .name(requestProduct.getName())
                .description(requestProduct.getDescription())
                .price(requestProduct.getPrice())
                .stock(requestProduct.getStock())
                .category(category)
                .user(user)
                .build();

        if (requestProduct.getProductImages() != null) {
            List<ProductImages> productImages = requestProduct.getProductImages().stream()
                    .map(strImage ->
                            ProductImages.builder().
                                    product(product)
                                    .imageUrl(strImage)
                                    .build())
                    .toList();
            product.setProductImages(productImages);
        }

        return productRepository.save(product);
    }

    private List<ProductOption> parseOptions(Product product, ProductOption parent, Map<String, Object> optionsMap) {
        List<ProductOption> createdOptions = new ArrayList<>();

        for (Map.Entry<String, Object> entry : optionsMap.entrySet()) {
            String optionName = entry.getKey();
            Object optionValue = entry.getValue();

            ProductOption currentOption = ProductOption.builder()
                    .product(product)
                    .name(optionName)
                    .parent(parent)
                    .build();

            // optionValue : {optionName, optionValue}
            // 아래에 다른 옵션이 있는 경우 재귀 호출로 더 깊이 찾기
            if (optionValue instanceof Map) {
                createdOptions.add(currentOption);

                List<ProductOption> childOptions = parseOptions(product, currentOption, (Map<String, Object>) optionValue);
                createdOptions.addAll(childOptions);
            }

            // optionValue : optionValue
            // 옵션에 대한 값이 있는 경우 재고로 판단
            else if (optionValue instanceof Integer) {
                int stock = (Integer) optionValue;
                ProductOption leafOption = ProductOption.builder()
                        .product(product)
                        .name(optionName)
                        .parent(parent)
                        .stock(stock)
                        .build();
                createdOptions.add(leafOption);
            }
        }
        return createdOptions;
    }

    public ProductDetailResponse get(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

        List<ProductOption> rootOptions = productOptionRepository.findByProductAndParentIsNull(product);

        Map<String, Object> optionsMap = new LinkedHashMap<>();
        for (ProductOption rootOption : rootOptions) {
            optionsMap.put(rootOption.getName(), buildOptionMap(rootOption));
        }

        return new ProductDetailResponse(product, optionsMap);

    }

    public PagingResponse<ProductResponse> getList(RequestPage requestPage) {
        Page<Product> postPage = productRepository.getList(requestPage);
        return new PagingResponse<>(postPage, ProductResponse.class);
    }


    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

        productRepository.delete(product);
    }

    private Object buildOptionMap(ProductOption option) {
        if (option.getChildren() == null || option.getChildren().isEmpty()) {
            return option.getStock();
        }

        Map<String, Object> childMap = new LinkedHashMap<>();
        for (ProductOption child : option.getChildren()) {
            childMap.put(child.getName(), buildOptionMap(child));
        }
        return childMap;
    }

}










