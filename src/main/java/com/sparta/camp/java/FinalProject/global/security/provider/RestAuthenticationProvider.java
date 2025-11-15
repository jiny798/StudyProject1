package com.sparta.camp.java.FinalProject.global.security.provider;

import com.sparta.camp.java.FinalProject.domain.user.dto.UserContext;
import com.sparta.camp.java.FinalProject.global.security.token.RestAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("restAuthenticationProvider")
public class RestAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("authenticate");
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserContext userContext = (UserContext) userDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, userContext.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new RestAuthenticationToken(userContext.getAuthorities(), userContext.getUser(), null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(RestAuthenticationToken.class);
    }
}
