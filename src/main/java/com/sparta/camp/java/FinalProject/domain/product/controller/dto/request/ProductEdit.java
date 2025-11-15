package com.sparta.camp.java.FinalProject.domain.product.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductEdit {
    @NotBlank(message = "제목을 입력하세요.")
    private String title;

    @NotBlank(message = "내용을 입력하세요.")
    private String content;

    @Builder
    public ProductEdit(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
