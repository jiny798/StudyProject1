package com.sparta.camp.java.FinalProject.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.camp.java.FinalProject.domain.category.entity.Category;
import com.sparta.camp.java.FinalProject.domain.category.repository.CategoryRepository;
import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import com.sparta.camp.java.FinalProject.domain.order.entity.OrderProduct;
import com.sparta.camp.java.FinalProject.domain.order.repository.OrderProductRepository;
import com.sparta.camp.java.FinalProject.domain.order.repository.OrderRepository;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import com.sparta.camp.java.FinalProject.domain.product.repository.ProductRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.jiny.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        cleanUp();
        // Create test data
        testUser = userRepository.findUserByEmail("testuser@test.com")
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Category category = categoryRepository.save(Category.builder().name("테스트 카테고리").build());

        testProduct = productRepository.save(Product.builder()
                .name("테스트 상품")
                .price(BigDecimal.valueOf(10000))
                .description("테스트 상품 설명")
                .user(testUser)
                .category(category)
                .stock(100)
                .build());

         testOrder = orderRepository.save(Order.builder()
                .user(testUser)
                .totalPrice(BigDecimal.valueOf(10000))
                .shippingAddress("테스트 배송지")
                .discountedPrice(BigDecimal.ZERO)
                .build());

        orderProductRepository.save(OrderProduct.builder()
                .order(testOrder)
                .product(testProduct)
                .quantity(1)
                .price(testProduct.getPrice())
                .build());
    }

    @AfterEach
    void tearDown() {
        cleanUp();
        userRepository.deleteAll();
    }

    private void cleanUp() {
        orderProductRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("주문 목록 조회")
    @WithUser("testuser@test.com")
    void getOrders() throws Exception {
        // expected
        mockMvc.perform(get("/api/orders")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("order-list",
                        responseFields(
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                fieldWithPath("message[]").description("주문 목록"),
                                fieldWithPath("message[].orderId").description("주문 ID"),
                                fieldWithPath("message[].totalPrice").description("주문 총액"),
                                fieldWithPath("message[].discountPrice").description("할인 금액"),
                                fieldWithPath("message[].products[]").description("주문 상품 목록"),
                                fieldWithPath("message[].products[].productId").description("상품 ID"),
                                fieldWithPath("message[].products[].productName").description("상품명"),
                                fieldWithPath("message[].products[].quantity").description("수량"),
                                fieldWithPath("message[].products[].totalPrice").description("개별 상품 총액")
                        )
                ));
    }


    @Test
    @DisplayName("주문 상세 조회")
    @WithUser("testuser@test.com")
    void getOrderDetail() throws Exception {
        // expected
        mockMvc.perform(get("/api/orders/{orderId}", testOrder.getId())
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("order-detail",
                        responseFields(
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                fieldWithPath("message.orderId").description("주문 ID"),
                                fieldWithPath("message.totalPrice").description("주문 총액"),
                                fieldWithPath("message.discountedPrice").description("할인 금액"),
                                fieldWithPath("message.shippingAddress").description("배송지"),
                                fieldWithPath("message.status").description("주문 상태"),
                                fieldWithPath("message.orderDate").description("주문 일시"),
                                fieldWithPath("message.products[]").description("주문 상품 목록"),
                                fieldWithPath("message.products[].productId").description("상품 ID"),
                                fieldWithPath("message.products[].productName").description("상품명"),
                                fieldWithPath("message.products[].quantity").description("수량"),
                                fieldWithPath("message.products[].price").description("개당 가격")
                        )
                ));
    }

    @Test
    @DisplayName("주문 취소")
    @WithUser("testuser@test.com")
    void cancelOrder() throws Exception {
        // expected
        mockMvc.perform(post("/api/orders/{orderId}/cancel", testOrder.getId())
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("order-cancel",
                        responseFields(
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                fieldWithPath("message.orderId").description("취소된 주문 ID"),
                                fieldWithPath("message.status").description("변경 된 주문 상태"),
                                fieldWithPath("message.cancelledAt").description("주문 취소 일시"),
                                fieldWithPath("message.message").description("취소 완료 메시지"),
                                fieldWithPath("message.cancelledProducts[]").description("취소된 상품 목록"),
                                fieldWithPath("message.cancelledProducts[].productId").description("취소된 상품 ID"),
                                fieldWithPath("message.cancelledProducts[].refundedPrice").description("환불된 상품 금액")
                        )));
    }
}