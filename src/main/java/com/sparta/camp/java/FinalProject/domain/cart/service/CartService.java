package com.sparta.camp.java.FinalProject.domain.cart.service;

import com.sparta.camp.java.FinalProject.common.exception.ServiceException;
import com.sparta.camp.java.FinalProject.common.exception.ServiceExceptionCode;
import com.sparta.camp.java.FinalProject.domain.cart.dto.CartItemUpdateRequest;
import com.sparta.camp.java.FinalProject.domain.cart.dto.CartItemResponse;
import com.sparta.camp.java.FinalProject.domain.cart.dto.CartResponse;
import com.sparta.camp.java.FinalProject.domain.cart.dto.CartItemAddRequest;
import com.sparta.camp.java.FinalProject.domain.cart.entity.Cart;
import com.sparta.camp.java.FinalProject.domain.cart.entity.CartItem;
import com.sparta.camp.java.FinalProject.domain.cart.repository.CartItemRepository;
import com.sparta.camp.java.FinalProject.domain.cart.repository.CartRepository;
import com.sparta.camp.java.FinalProject.domain.product.entity.Product;
import com.sparta.camp.java.FinalProject.domain.product.repository.ProductRepository;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import com.sparta.camp.java.FinalProject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addCartItem(Long userId, CartItemAddRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });

        // 이미 장바구니에 있는 상품인지 확인
        cartItemRepository.findByCartAndProduct(cart, product)
                .ifPresentOrElse(
                        // 이미 있으면 수량만 증가
                        cartItem -> cartItem.addQuantity(request.getQuantity()),
                        // 없으면 새로 추가
                        () -> {
                            CartItem newCartItem = CartItem.builder()
                                    .cart(cart)
                                    .product(product)
                                    .quantity(request.getQuantity())
                                    .build();
                            cartItemRepository.save(newCartItem);
                        }
                );
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));

        // Fetch join을 사용하여 N+1 문제 방지
        Cart cart = cartRepository.findByUser(user).orElse(null);

        if (cart == null || cart.getCartItems().isEmpty()) {
            return CartResponse.of(Collections.emptyList(), BigDecimal.ZERO);
        }

        List<CartItemResponse> cartItemResponses = cart.getCartItems().stream()
                .map(CartItemResponse::from)
                .toList();

        BigDecimal totalPrice = cartItemResponses.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.of(cartItemResponses, totalPrice);
    }

    @Transactional
    public void updateCartItemQuantity(Long userId, Long cartItemId, CartItemUpdateRequest request) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CART_ITEM));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new ServiceException(ServiceExceptionCode.FORBIDDEN_ACCESS);
        }

        cartItem.updateQuantity(request.getQuantity());
    }

    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CART_ITEM));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new ServiceException(ServiceExceptionCode.FORBIDDEN_ACCESS);
        }

        cartItemRepository.delete(cartItem);
    }
}