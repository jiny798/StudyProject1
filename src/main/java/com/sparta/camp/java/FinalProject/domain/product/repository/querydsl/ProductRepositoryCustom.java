package com.sparta.camp.java.FinalProject.domain.product.repository.querydsl;

import com.sparta.camp.java.FinalProject.common.page.RequestPage;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import org.springframework.data.domain.Page;

public interface ProductRepositoryCustom {

    Page<Product> getList(RequestPage requestPage);
}
