package com.sparta.camp.java.FinalProject.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.camp.java.FinalProject.global.security.filter.RestAuthenticationFilter;
import com.sparta.camp.java.FinalProject.global.security.handler.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

import javax.sql.DataSource;


@Slf4j
@EnableWebSecurity
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final int rememberMeValidTimeout = 3600 * 24 * 30;
    
    private final DataSource dataSource;
    private final ObjectMapper objectMapper;
    private final AuthenticationProvider restAuthenticationProvider;

    @Bean
    @Order(0)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(restAuthenticationProvider);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        SpaCsrfTokenRequestHandler csrfTokenRequestHandler = new SpaCsrfTokenRequestHandler();
        http
                .securityMatcher("/**")
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/uploads/**", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/error").permitAll()
//                                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                                .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(restAuthenticationFilter(http, authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager)
                .exceptionHandling(e -> {
                    e.accessDeniedHandler(new Http403Handler(objectMapper));
                    e.authenticationEntryPoint(new Http401Handler(objectMapper));
                })
                .rememberMe(rm -> rm.rememberMeParameter("remember")
                        .alwaysRemember(false)
                        .tokenValiditySeconds(2592000)
                );

        return http.build();
    }

    public RestAuthenticationFilter restAuthenticationFilter(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) {
        RestAuthenticationFilter filter = new RestAuthenticationFilter(httpSecurity, "/api/login", objectMapper);
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(new LoginSuccessHandler(objectMapper));
        filter.setAuthenticationFailureHandler(new LoginFailHandler(objectMapper));
        filter.setSecurityContextRepository(getSecurityContextRepository(httpSecurity));

        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(true);
        rememberMeServices.setValiditySeconds(rememberMeValidTimeout);
        filter.setRememberMeServices(rememberMeServices);
        return filter;
    }

    @Bean
    public SecurityContextRepository getSecurityContextRepository(HttpSecurity httpSecurity) {
        SecurityContextRepository sharedRepository = httpSecurity.getSharedObject(SecurityContextRepository.class);
        log.info("SharedRepository: {}", sharedRepository);
        if (sharedRepository == null) {
            sharedRepository = new DelegatingSecurityContextRepository(
                    new RequestAttributeSecurityContextRepository(), securityContextRepository()
            );
        }
        return sharedRepository;
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setAllowSessionCreation(true); // 인증 성공 시 세션 생성 허용
        return repo; // Spring Session이 HttpSession을 Redis로 대체하므로 자동 연동
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

}