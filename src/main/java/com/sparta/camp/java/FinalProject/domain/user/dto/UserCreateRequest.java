package com.sparta.camp.java.FinalProject.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {

    @NotNull
    String name;

    @Email
    String email;

    @NotNull
    String password;

    @NotNull
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "휴대폰 번호 양식이 올바르지 않습니다.")
    String phoneNumber;

}