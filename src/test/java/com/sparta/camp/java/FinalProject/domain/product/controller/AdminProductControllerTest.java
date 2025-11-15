package com.sparta.camp.java.FinalProject.domain.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.camp.java.FinalProject.domain.category.entity.Category;
import com.sparta.camp.java.FinalProject.domain.category.repository.CategoryRepository;
import com.sparta.camp.java.FinalProject.domain.product.controller.dto.request.RequestProduct;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import com.sparta.camp.java.FinalProject.domain.product.repository.ProductRepository;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRepository;
import com.sparta.camp.java.FinalProject.security.WithAdmin;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.jiny.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Category testCategory;
    private User testUser;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        testCategory = categoryRepository.save(Category.builder().name("테스트 카테고리").build());
        testUser = userRepository.save(User.builder()
                .email("admin@test.com")
                .passwordHash(passwordEncoder.encode("password"))
                .name("Test Admin")
                .build());
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 등록")
    @WithAdmin("admin@test.com")
    void postProduct() throws Exception {
        // given
        Map<String, Object> options = Map.of(
                "색상", Map.of(
                        "네이비", Map.of("사이즈", Map.of("M", 10, "L", 5)),
                        "블랙", Map.of("사이즈", Map.of("M", 20))
                )
        );

        RequestProduct request = RequestProduct.builder()
                .name("상품 제목 테스트4")
                .price(BigDecimal.valueOf(50000))
                .stock(3)
                .categoryId(testCategory.getId())
                .description("상품 설명 테스트4")
                .productImages(List.of(
                        "https://example.com/image_url_1.jpg",
                        "https://example.com/image_url_2.png",
                        "https://example.com/image_url_3.gif"
                ))
                .options(options)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/admin/products")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("product-create",
                        requestFields(
                                fieldWithPath("name").description("상품명"),
                                fieldWithPath("price").description("가격"),
                                fieldWithPath("stock").description("기본 재고"),
                                fieldWithPath("categoryId").description("카테고리 ID"),
                                fieldWithPath("description").description("상품 설명"),
                                fieldWithPath("productImages").description("상품 이미지 URL 목록"),
                                fieldWithPath("options").description("상품 옵션 (자유로운 JSON 객체)"),
                                fieldWithPath("options.색상").description("옵션 그룹 (예: 색상)"),
                                fieldWithPath("options.색상.네이비").description("첫 번째 옵션 (예: 네이비)"),
                                fieldWithPath("options.색상.네이비.사이즈").description("하위 옵션 그룹 (예: 사이즈)"),
                                fieldWithPath("options.색상.네이비.사이즈.M").description("하위 옵션 값과 재고 (예: M 사이즈 재고)"),
                                fieldWithPath("options.색상.네이비.사이즈.L").description("하위 옵션 값과 재고 (예: L 사이즈 재고)"),
                                fieldWithPath("options.색상.블랙").description("두 번째 옵션 (예: 블랙)"),
                                fieldWithPath("options.색상.블랙.사이즈").description("하위 옵션 그룹 (예: 사이즈)"),
                                fieldWithPath("options.색상.블랙.사이즈.M").description("하위 옵션 값과 재고 (예: M 사이즈 재고)")
                        ),
                        responseFields(
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                fieldWithPath("message").description("응답 메시지 (성공 시 null)")
                        )
                ));
    }

    @Test
    @DisplayName("상품 목록 조회")
    @WithAdmin("admin@test.com")
    void getList() throws Exception {
        // given
        List<Product> products = IntStream.range(1, 31)
                .mapToObj(i -> Product.builder()
                        .name("상품 " + i)
                        .price(BigDecimal.valueOf(1000 * i))
                        .description("상품 설명 " + i)
                        .category(testCategory)
                        .user(testUser)
                        .stock(i * 10)
                        .build())
                .toList();
        productRepository.saveAll(products);

        // expected
        mockMvc.perform(get("/api/admin/products")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("product-list",
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                parameterWithName("size").description("페이지 당 상품 수")
                        ),
                        responseFields(
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                fieldWithPath("message.page").description("현재 페이지 번호 (1부터 시작)"),
                                fieldWithPath("message.size").description("페이지 당 아이템 수"),
                                fieldWithPath("message.totalCount").description("전체 아이템 수"),
                                fieldWithPath("message.items[]").description("상품 목록"),
                                fieldWithPath("message.items[].id").description("상품 ID"),
                                fieldWithPath("message.items[].name").description("상품명"),
                                fieldWithPath("message.items[].price").description("가격"),
                                fieldWithPath("message.items[].options").description("상품 옵션"),
                                fieldWithPath("message.items[].description").description("상품 설명"),
                                fieldWithPath("message.items[].productImages").description("상품 이미지 URL 목록")
                        )
                ));
    }
}