package com.sparta.camp.java.FinalProject.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.camp.java.FinalProject.domain.user.dto.UserCreateRequest;
import com.sparta.camp.java.FinalProject.domain.user.entity.Role;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import com.sparta.camp.java.FinalProject.domain.user.entity.UserRole;
import com.sparta.camp.java.FinalProject.domain.user.repository.RoleRepository;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRepository;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebSecurity
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.jiny.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
class UserControllerDocTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @AfterEach
    void clean() {
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 가입")
    void signUp() throws Exception {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
                .name("Tester")
                .email("Tester@jiny.com")
                .password("Tester1")
                .phoneNumber("010-1111-2222")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-singUp",
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("email").description("이메일").attributes(key("constraint").value("이메일 형식")),
                                fieldWithPath("password").description("패스워드").attributes(key("constraint").value("영문, 숫자 조합")),
                                fieldWithPath("phoneNumber").description("휴대 전화").attributes(key("constraint").value("하이픈(-) 포함"))
                        ),
                        responseFields( // 실제 응답 구조에 맞게 수정
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                fieldWithPath("message.id").description("사용자 고유 ID"),
                                fieldWithPath("message.name").description("이름"),
                                fieldWithPath("message.email").description("이메일"),
                                fieldWithPath("message.createdAt").description("가입일시")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception {
        // given
        User user = User.builder()
                .email("test22@test.com")
                .passwordHash(passwordEncoder.encode("test22"))
                .name("로그인테스터")
                .phone("010-0000-0000") // phone 필드 추가
                .build();
        userRepository.saveAndFlush(user);

        Role role = roleRepository.findByRoleName("ROLE_USER");
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role).build();
        userRoleRepository.saveAndFlush(userRole);

        String json = """
                {
                    "email" : "test22@test.com",
                    "password" : "test22"
                }
                """;

        // expected
        mockMvc.perform(post("/api/login")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-login",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("패스워드")
                        )));
    }
}