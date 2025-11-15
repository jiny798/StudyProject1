package com.sparta.camp.java.FinalProject.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPasswordUpdateRequest {

    @NotNull
    String beforePassword;

    @NotNull
    String password;

    @NotNull
    String passwordConfirm;

}