package com.sparta.camp.java.FinalProject.domain.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.request.CreateCouponRequest;
import com.sparta.camp.java.FinalProject.domain.coupon.controller.dto.request.IssueCouponRequest;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.Coupon;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.type.DiscountType;
import com.sparta.camp.java.FinalProject.domain.coupon.entity.type.ExpirationType;
import com.sparta.camp.java.FinalProject.domain.coupon.repository.CouponRepository;
import com.sparta.camp.java.FinalProject.domain.coupon.repository.UserCouponRepository;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRepository;
import com.sparta.camp.java.FinalProject.security.WithAdmin;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.jiny.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
class AdminCouponControllerTest {

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
        testUser = userRepository.save(User.builder()
                .email("testuser@test.com")
                .passwordHash("password")
                .name("Test User")
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
    @WithAdmin("admin@test.com")
    @DisplayName("[관리자] 쿠폰 생성 성공")
    void createCoupon() throws Exception {
        // given
        CreateCouponRequest request = CreateCouponRequest.builder()
                .name("신규 가입 환영 쿠폰")
                .description("가입해주셔서 감사합니다!")
                .discountType(DiscountType.FIXED)
                .discountValue(BigDecimal.valueOf(3000))
                .expirationType(ExpirationType.VALID_DAYS_ON_ISSUE)
                .totalQuantity(100)
                .validDays(30)
                .build();

        // when & then
        mockMvc.perform(post("/api/admin/coupons")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-coupon-create",
                        requestFields(
                                fieldWithPath("name").description("쿠폰 이름"),
                                fieldWithPath("description").description("쿠폰 설명"),
                                fieldWithPath("discountType").description("할인 종류 (FIXED, PERCENTAGE)"),
                                fieldWithPath("discountValue").description("할인 값 (금액 또는 비율)"),
                                fieldWithPath("expirationType").description("만료 종류 (VALID_DAYS_ON_ISSUE, FIXED_PERIOD)"),
                                fieldWithPath("validDays").description("유효 일수 (발급일 기준)").optional(),
                                fieldWithPath("startDate").description("고정 기간 시작일").optional(),
                                fieldWithPath("endDate").description("고정 기간 종료일").optional(),
                                fieldWithPath("minOrderAmount").description("최소 주문 금액").optional(),
                                fieldWithPath("maxDiscountAmount").description("최대 할인 금액").optional(),
                                fieldWithPath("totalQuantity").description("총 발급 가능 수량").optional()
                        ),
                        responseFields(
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                fieldWithPath("message.id").description("생성된 쿠폰 ID"),
                                fieldWithPath("message.name").description("쿠폰 이름"),
                                fieldWithPath("message.discountValue").description("할인 값"),
                                fieldWithPath("message.totalQuantity").description("수량"),
                                fieldWithPath("message.status").description("쿠폰 상태")
                        )
                ));
    }

    @Test
    @WithAdmin("admin@test.com")
    @DisplayName("[관리자] 쿠폰 비활성화(삭제)")
    void deleteCoupon() throws Exception {
        // given
        Coupon coupon = couponRepository.save(CreateCouponRequest.builder()
                .name("삭제될 쿠폰")
                .discountType(DiscountType.FIXED)
                .description("이 쿠폰은 테스트 후 삭제됩니다.")
                .discountValue(BigDecimal.ONE)
                .expirationType(ExpirationType.VALID_DAYS_ON_ISSUE)
                .validDays(1)
                .build().toEntity());

        // when & then
        mockMvc.perform(delete("/api/admin/coupons/{couponId}", coupon.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-coupon-delete",
                        pathParameters(
                                parameterWithName("couponId").description("비활성화할 쿠폰 ID")
                        ),
                        responseFields(
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                fieldWithPath("message").description("응답 메시지 (null)").optional()
                        )
                ));
    }

    @Test
    @WithAdmin("admin@test.com")
    @DisplayName("[관리자] 특정 사용자에게 쿠폰 발급")
    void issueCouponToUser() throws Exception {
        // given
        Coupon coupon = couponRepository.save(CreateCouponRequest.builder()
                .name("발급될 쿠폰")
                .description("발급될 쿠폰")
                .discountType(DiscountType.FIXED)
                .discountValue(BigDecimal.ONE)
                .expirationType(ExpirationType.VALID_DAYS_ON_ISSUE)
                .validDays(1)
                .build().toEntity());

        IssueCouponRequest request = IssueCouponRequest.builder()
                .userId(testUser.getId())
                .couponId(coupon.getId())
                .build();

        // when & then
        mockMvc.perform(post("/api/admin/coupons/issue")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-coupon-issue",
                        requestFields(
                                fieldWithPath("userId").description("쿠폰을 발급받을 사용자 ID"),
                                fieldWithPath("couponId").description("발급할 쿠폰 ID")
                        ),
                        responseFields(
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                fieldWithPath("message").description("응답 메시지 (null)").optional()
                        )
                ));
    }
}