package com.sparta.camp.java.FinalProject.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceException extends RuntimeException {

    int statusCode;
    String errorCode;
    String message;

    public ServiceException(ServiceExceptionCode response) {
        super(response.getMessage());
        this.statusCode = response.getStatusCode();
        this.errorCode = response.name();
        this.message = super.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }
}