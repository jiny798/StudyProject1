package com.sparta.camp.java.FinalProject.domain.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.Coupon;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.UserCoupon;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.type.DiscountType;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.type.ExpirationType;
import com.sparta.camp.java.FinalProject.domain.coupon.repository.CouponRepository;
import com.sparta.camp.java.FinalProject.domain.coupon.repository.UserCouponRepository;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRepository;
import com.sparta.camp.java.FinalProject.security.WithUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.jiny.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        cleanUp();
        testUser = userRepository.findUserByEmail("testuser@test.com")
                .orElseThrow(() -> new RuntimeException("User not found"));

        Coupon couponTemplate = couponRepository.save(Coupon.builder()
                .name("테스트 쿠폰")
                .description("테스트용 쿠폰입니다.")
                .discountType(DiscountType.FIXED)
                .discountValue(BigDecimal.valueOf(1000))
                .expirationType(ExpirationType.VALID_DAYS_ON_ISSUE)
                .validDays(30)
                .build());

        // 1. 사용 가능한 쿠폰
        userCouponRepository.save(UserCoupon.builder()
                .user(testUser)
                .coupon(couponTemplate)
                .isUsed(false)
                .expirationDate(LocalDateTime.now().plusDays(10))
                .build());

        // 2. 이미 사용한 쿠폰
        userCouponRepository.save(UserCoupon.builder()
                .user(testUser)
                .coupon(couponTemplate)
                .isUsed(true)
                .expirationDate(LocalDateTime.now().plusDays(10))
                .build());

        // 3. 만료된 쿠폰
        userCouponRepository.save(UserCoupon.builder()
                .user(testUser)
                .coupon(couponTemplate)
                .isUsed(false)
                .expirationDate(LocalDateTime.now().minusDays(1))
                .build());
    }

    @AfterEach
    void tearDown() {
        cleanUp();
        userRepository.deleteAll();
    }

    private void cleanUp() {
        userCouponRepository.deleteAll();
        couponRepository.deleteAll();
    }

    @Test
    @DisplayName("[사용자] 내 쿠폰 목록 조회")
    @WithUser("testuser@test.com")
    void getMyAvailableCoupons() throws Exception {
        // when & then
        mockMvc.perform(get("/api/coupons/my")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message.length()").value(1)) // 사용 가능한 쿠폰은 1개여야 함
                .andDo(document("user-coupon-list",
                        responseFields(
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                fieldWithPath("message[]").description("사용 가능한 내 쿠폰 목록"),
                                fieldWithPath("message[].userCouponId").description("내가 보유한 쿠폰의 고유 ID"),
                                fieldWithPath("message[].name").description("쿠폰 이름"),
                                fieldWithPath("message[].description").description("쿠폰 설명"),
                                fieldWithPath("message[].discountType").description("할인 종류"),
                                fieldWithPath("message[].discountValue").description("할인 값"),
                                fieldWithPath("message[].used").description("사용 여부 (false)"),
                                fieldWithPath("message[].issuedAt").description("발급 일시"),
                                fieldWithPath("message[].expirationDate").description("만료 일시")
                        )
                ));
    }
}