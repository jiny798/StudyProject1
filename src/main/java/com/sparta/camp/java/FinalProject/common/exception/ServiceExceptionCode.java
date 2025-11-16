package com.sparta.camp.java.FinalProject.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ServiceExceptionCode {

    // User
    DUPLICATED_EMAIL(400, "이미 사용 중인 이메일입니다."),
    SIGNUP_PASSWORD_CONFIRM_FAIL(400, "패스워드가 일치하지 않습니다."),
    NOT_FOUND_USER(404, "유저를 찾을 수 없습니다."),
    UNAUTHORIZED(401, "로그인이 필요합니다."),
    LOGIN_FAIL(401, "아이디 혹은 비밀번호가 일치하지 않습니다."),
    FORBIDDEN(403, "권한 필요"),
    FORBIDDEN_ACCESS(403, "접근 권한이 없습니다."),

    // Product
    NOT_FOUND_PRODUCT(404, "상품을 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(400, "상품의 재고가 부족합니다."),
    INVALID_PRODUCT_OPTION(400, "옵션 형식이 잘못되었습니다."),

    //Category
    NOT_FOUND_CATEGORY(404, "상품을 찾을 수 없습니다."),
    CATEGORY_PARENT_CYCLE(400, "부모 카테고리와 ID 값이 동일할 수 없습니다."),
    NOT_FOUND_CART(400, "유효하지 않은 장바구니입니다."),
    NOT_FOUND_CART_ITEM(404, "장바구니 상품을 찾을 수 없습니다."),
    OUT_OF_STOCK_PRODUCT(400, "재고 수량이 없습니다."),
    NOT_FOUND_PURCHASE(400, "주문 내역을 확인 할 수 없습니다."),
    CANNOT_CANCEL(400, "취소 불가능한 상태입니다."),
    INVALID_CATEGORY_DATA(400, "현재 카테고리와 상위 카테고리가 동일합니다."),

    //Coupon
    NOT_FOUND_COUPON(404, "쿠폰을 찾을 수 없습니다."),
    EXPIRED_COUPON(400,"기간이 만료된 쿠폰입니다."),
    COUPON_NOT_ACTIVE(400, "현재 발급 가능하지 않은 쿠폰입니다."),
    COUPON_OUT_OF_STOCK(400, "쿠폰이 모두 소진되었습니다."),
    INVALID_COUPON_EXPIRATION_TYPE(500, "유효하지 않은 쿠폰 만료 타입입니다."),
    INVALID_COUPON_DATE_RANGE(400, "쿠폰 유효 기간 설정이 올바르지 않습니다."),
    INVALID_COUPON_VALID_DAYS(400, "쿠폰 유효 일수 설정이 올바르지 않습니다."),

    //User Coupon
    ALREADY_USED_COUPON(400, "이미 사용된 쿠폰입니다."),
    NOT_FOUND_USER_COUPON(404, "해당 쿠폰이 없습니다."),
    COUPON_OWNERSHIP_MISMATCH(403, "해당 쿠폰을 사용할 수 없습니다"),

    //Order
    NOT_FOUND_ORDER(404, "유효하지 않은 주문입니다."),
    CANCEL_FAIL(400, "주문을 취소할 수 없습니다.");


    final int statusCode;
    final String message;
}