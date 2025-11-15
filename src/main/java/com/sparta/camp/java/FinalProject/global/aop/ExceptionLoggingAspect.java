package com.sparta.camp.java.FinalProject.global.aop;

import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExceptionLoggingAspect {

    // Pointcut: Service 계층의 모든 메서드를 대상으로 지정
    @Pointcut("execution(* com.sparta.camp.java.FinalProject.domain..service..*(..))")
    private void allServiceMethods() {
    }

    // Advice: serviceMethods Pointcut에서 ServiceException 타입의 예외 발생 시 동작
    @AfterThrowing(pointcut = "allServiceMethods()", throwing = "exception")
    public void logServiceException(ServiceException exception) {
        log.error("Service Layer Exception: Code = [{}], Message = [{}]",
                exception.getErrorCode(), exception.getMessage());
    }

}