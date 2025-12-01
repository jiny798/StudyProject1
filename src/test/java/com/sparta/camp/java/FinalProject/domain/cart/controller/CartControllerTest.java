package com.sparta.camp.java.FinalProject.domain.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.camp.java.FinalProject.domain.cart.controller.dto.CartItemAddRequest;
import com.sparta.camp.java.FinalProject.domain.cart.controller.dto.CartItemUpdateRequest;
import com.sparta.camp.java.FinalProject.domain.cart.entity.Cart;
import com.sparta.camp.java.FinalProject.domain.cart.entity.CartItem;
import com.sparta.camp.java.FinalProject.domain.cart.repository.CartItemRepository;
import com.sparta.camp.java.FinalProject.domain.cart.repository.CartRepository;
import com.sparta.camp.java.FinalProject.domain.category.entity.Category;
import com.sparta.camp.java.FinalProject.domain.category.repository.CategoryRepository;
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
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.jiny.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
class CartControllerTest {

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
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        cleanUp();
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
    }

    @AfterEach
    void tearDown() {
        cleanUp();
        userRepository.deleteAll();
    }

    private void cleanUp() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("장바구니에 상품 추가")
    @WithUser("testuser@test.com")
    void addCartItem() throws Exception {
        // given
        CartItemAddRequest request = new CartItemAddRequest();
        request.setProductId(testProduct.getId());
        request.setQuantity(2);

        // when & then
        mockMvc.perform(post("/api/cart")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("cart-add-item",
                        requestFields(
                                fieldWithPath("productId").description("상품 ID"),
                                fieldWithPath("quantity").description("수량")
                        ),
                        responseFields(
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("message").description("메시지 정보 (성공 시 null)"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)")
                        )
                ));
    }

    @Test
    @DisplayName("장바구니 조회")
    @WithUser("testuser@test.com")
    void getCart() throws Exception {
        // given: 장바구니에 상품 추가
        addCartItem();

        // when & then
        mockMvc.perform(get("/api/cart")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("cart-get-items",
                        responseFields(
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                fieldWithPath("message.cartItems[]").description("장바구니 상품 목록"),
                                fieldWithPath("message.cartItems[].cartItemId").description("장바구니 상품 ID"),
                                fieldWithPath("message.cartItems[].productId").description("상품 ID"),
                                fieldWithPath("message.cartItems[].productName").description("상품명"),
                                fieldWithPath("message.cartItems[].quantity").description("수량"),
                                fieldWithPath("message.cartItems[].price").description("개당 가격"),
                                fieldWithPath("message.totalPrice").description("장바구니 총액")
                        )
                ));
    }

    @Test
    @DisplayName("장바구니 상품 수량 변경")
    @WithUser("testuser@test.com")
    void updateCartItemQuantity() throws Exception {
        // given
        Cart cart = cartRepository.save(Cart.builder().user(testUser).build());
        CartItem cartItem = cartItemRepository.save(CartItem.builder().cart(cart).product(testProduct).quantity(1).build());

        CartItemUpdateRequest request = new CartItemUpdateRequest();
        request.setQuantity(5);

        // when & then
        mockMvc.perform(patch("/api/cart/items/{cartItemId}", cartItem.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("cart-update-item-quantity",
                        pathParameters(
                                parameterWithName("cartItemId").description("장바구니 상품 ID")
                        ),
                        requestFields(
                                fieldWithPath("quantity").description("변경할 수량")
                        ),
                        responseFields(
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("message").description("메시지 정보 (성공 시 null)"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)")
                        )
                ));
    }

    @Test
    @DisplayName("장바구니 상품 삭제")
    @WithUser("testuser@test.com")
    void removeCartItem() throws Exception {
        // given
        Cart cart = cartRepository.save(Cart.builder().user(testUser).build());
        CartItem cartItem = cartItemRepository.save(CartItem.builder().cart(cart).product(testProduct).quantity(1).build());

        // when & then
        mockMvc.perform(delete("/api/cart/items/{cartItemId}", cartItem.getId())
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("cart-remove-item",
                        pathParameters(
                                parameterWithName("cartItemId").description("장바구니 상품 ID")
                        ),
                        responseFields(
                                fieldWithPath("result").description("성공 여부"),
                                fieldWithPath("message").description("메시지 정보 (성공 시 null)"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)")
                        )
                ));
    }
}