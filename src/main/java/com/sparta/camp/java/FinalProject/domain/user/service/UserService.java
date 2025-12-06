package com.sparta.camp.java.FinalProject.domain.user.service;

import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestPage;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.PagingResponse;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.response.ProductResponse;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import com.sparta.camp.java.FinalProject.domain.user.dto.UserCreateRequest;
import com.sparta.camp.java.FinalProject.domain.user.dto.UserPasswordUpdateRequest;
import com.sparta.camp.java.FinalProject.domain.user.dto.UserResponse;
import com.sparta.camp.java.FinalProject.domain.user.entity.Role;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import com.sparta.camp.java.FinalProject.domain.user.entity.UserRole;
import com.sparta.camp.java.FinalProject.domain.user.mapper.UserMapper;
import com.sparta.camp.java.FinalProject.domain.user.repository.RoleRepository;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserQueryRepository;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRepository;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse signUp(UserCreateRequest request) {
        User findUser = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> null);
        if (findUser != null) {
            new ServiceException(ServiceExceptionCode.DUPLICATED_EMAIL);
        }

        User user = userRepository.save(
                User.builder().
                        name(request.getName())
                        .email(request.getEmail())
                        .passwordHash(passwordEncoder.encode(request.getPassword()))
                        .phone(request.getPhoneNumber())
                        .build()
        );

        Role role = roleRepository.findByRoleName("ROLE_USER");
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role).build();

        userRoleRepository.save(userRole);

        return userMapper.toResponse(user);
    }

    @Transactional
    public void update(Long userId, UserPasswordUpdateRequest request) {
        User user = getUser(userId);

        if (!passwordEncoder.matches(request.getBeforePassword(), user.getPasswordHash())) {
            new ServiceException(ServiceExceptionCode.SIGNUP_PASSWORD_CONFIRM_FAIL);
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            new ServiceException(ServiceExceptionCode.SIGNUP_PASSWORD_CONFIRM_FAIL);

        }

        userRepository.save(user);
    }

    @Transactional
    public void delete(Long userId) {
        userRepository.delete(getUser(userId));
    }

    public PagingResponse<UserResponse> getUserList(RequestPage requestPage) {
        Page<User> userPage = userRepository.getList(requestPage);
        return new PagingResponse<>(userPage, UserResponse.class);

    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));
    }


}