package com.sparta.camp.java.FinalProject.global.config;

import com.sparta.camp.java.FinalProject.domain.user.entity.Role;
import com.sparta.camp.java.FinalProject.domain.user.repository.RoleRepository;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
//@Profile({"prod", "dev"}) // "prod" 또는 "dev" 프로필에서만 실행되도록 설정
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private boolean alreadySetup = false;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final String ROLE_USER = "ROLE_USER";
    private final String ROLE_ADMIN = "ROLE_ADMIN";

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }
        setupData();
        alreadySetup = true;
    }

    private void setupData() {
        Role role = roleRepository.findByRoleName(ROLE_USER);
        if (role != null) {
            return;
        }

        createRole(ROLE_USER);
        createRole(ROLE_ADMIN);
    }

    private void createRole(String roleName) {
        Role role = Role.builder()
                .roleName(roleName)
                .build();
        roleRepository.save(role);
    }

}