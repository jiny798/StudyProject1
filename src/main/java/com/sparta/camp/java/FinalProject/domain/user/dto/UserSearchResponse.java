package com.sparta.camp.java.FinalProject.domain.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSearchResponse {

    Long id;

    String name;

    String email;

    LocalDateTime createdAt;

}