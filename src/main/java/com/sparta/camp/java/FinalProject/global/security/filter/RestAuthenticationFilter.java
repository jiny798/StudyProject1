package com.sparta.camp.java.FinalProject.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.camp.java.FinalProject.global.security.token.RestAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
public class RestAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    public RestAuthenticationFilter(HttpSecurity httpSecurity, String loginUrl, ObjectMapper objectMapper) {
        super(loginUrl);
        this.objectMapper = objectMapper;
        setSecurityContextRepository(getSecurityContextRepository(httpSecurity));
    }

    private SecurityContextRepository getSecurityContextRepository(HttpSecurity httpSecurity) {
        SecurityContextRepository sharedRepository = httpSecurity.getSharedObject(SecurityContextRepository.class);
        log.info("SharedRepository: {}", sharedRepository);
        if (sharedRepository == null) {
            sharedRepository = new DelegatingSecurityContextRepository(
                    new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository()
            );
        }
        return sharedRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            throw new IllegalArgumentException("HTTP method not supported: " + request.getMethod());
        }

        LoginInfo loginInfo = objectMapper.readValue(request.getInputStream(), LoginInfo.class);
        log.info("[LoginInfo] {}", loginInfo);
        if (!StringUtils.hasText(loginInfo.getEmail()) || !StringUtils.hasText(loginInfo.getPassword())) {
            throw new IllegalArgumentException("Email or password not provided");
        }

        RestAuthenticationToken authToken = new RestAuthenticationToken(loginInfo.getEmail(), loginInfo.getPassword());

        return this.getAuthenticationManager().authenticate(authToken);
    }

    @ToString
    @Getter
    private static class LoginInfo {
        private String email;
        private String password;
    }
}
