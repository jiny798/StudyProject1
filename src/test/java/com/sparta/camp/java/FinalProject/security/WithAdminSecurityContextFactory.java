package com.sparta.camp.java.FinalProject.security;

import com.sparta.camp.java.FinalProject.domain.user.dto.UserContext;
import com.sparta.camp.java.FinalProject.domain.user.dto.UserCreateRequest;
import com.sparta.camp.java.FinalProject.domain.user.service.SecurityUserService;
import com.sparta.camp.java.FinalProject.domain.user.service.UserService;
import com.sparta.camp.java.FinalProject.global.security.token.RestAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;
import java.util.List;

public class WithAdminSecurityContextFactory implements WithSecurityContextFactory<WithAdmin> {

    private final SecurityUserService securityUserService;
    private final UserService userService;

    public WithAdminSecurityContextFactory(SecurityUserService securityUserService, UserService userService) {
        this.securityUserService = securityUserService;
        this.userService = userService;
    }

    @Override
    public SecurityContext createSecurityContext(WithAdmin annotation) {
        String[] emails = annotation.value();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // 마지막 계정이 컨트롤러 인자 @CurrentUser 에 전달된다
        int count = 0;
        for (String email : emails) {
            count++;
            UserContext userContext = null;
            try {
                userContext = (UserContext) securityUserService.loadUserByUsername(email);
            } catch (UsernameNotFoundException exception) {
                UserCreateRequest request = new UserCreateRequest();
                request.setName("test" + count);
                request.setEmail(email);
                request.setPassword("jiny1234");
                userService.signUp(request);
                userContext = (UserContext) securityUserService.loadUserByUsername(email);
            }

            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
            Authentication authentication = new RestAuthenticationToken(authorities, userContext.getUser(), null);

            context.setAuthentication(authentication);
        }
        return context;
    }
}