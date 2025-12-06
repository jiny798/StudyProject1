package com.sparta.camp.java.FinalProject.domain.user.repository.querydsl;

import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestPage;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import org.springframework.data.domain.Page;

public interface UserRepositoryCustom {
    Page<User> getList(RequestPage requestPage);

}
