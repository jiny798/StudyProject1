CREATE TABLE users
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    phone         VARCHAR(30) UNIQUE,
    created_at    DATETIME              DEFAULT CURRENT_TIMESTAMP,
    updated_at    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE role
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL
);

CREATE TABLE user_role
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);

CREATE TABLE persistent_logins
(
    series    VARCHAR(64) PRIMARY KEY,
    username  VARCHAR(64) NOT NULL COMMENT '회원 로그인 아이디',
    token     VARCHAR(64) NOT NULL,
    last_used DATETIME(6) NOT NULL
);

CREATE TABLE orders
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '주문 고유 번호',
    user_id          BIGINT         NOT NULL COMMENT '회원 고유 번호',
    total_price      DECIMAL(10, 2) NOT NULL COMMENT '총가격',
    discounted_price      DECIMAL(10, 2) NOT NULL COMMENT '할인가격',
    status           ENUM('PENDING', 'COMPLETED', 'CANCELED') NOT NULL COMMENT '배송 상태',
    shipping_address TEXT           NOT NULL COMMENT '배송 주소',
    created_at       DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at       DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE order_product
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT         NOT NULL COMMENT '어떤 주문에 속하는지',
    product_id BIGINT         NOT NULL COMMENT '어떤 상품인지',
    price      DECIMAL(10, 2) NOT NULL COMMENT '주문 시점의 상품 가격',
    count BIGINT         NOT NULL COMMENT '개수',
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE cart
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL UNIQUE COMMENT '회원 고유 번호',
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE cart_item
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id    BIGINT NOT NULL COMMENT '장바구니 고유 번호',
    product_id BIGINT NOT NULL COMMENT '상품 고유 번호',
    quantity   INT    NOT NULL COMMENT '수량',
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE product
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT         NOT NULL,
    category_id BIGINT         NOT NULL,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2) NOT NULL,
--     stock       INT            NOT NULL,
    created_at  DATETIME                DEFAULT CURRENT_TIMESTAMP,
    updated_at  datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE product_options
(
    option_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT       NOT NULL COMMENT '이 옵션이 속한 상품 ID',
    parent_id  BIGINT COMMENT '부모 옵션 ID (최상위 옵션은 NULL)',
    name       VARCHAR(255) NOT NULL COMMENT '옵션 이름 (예: 색상, L, 블랙)',
    stock      INT          NOT NULL DEFAULT 0 COMMENT '재고 수량 (마지막 뎁스의 옵션에만 사용)'
);

CREATE TABLE product_images
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT       NOT NULL,
    image_url  VARCHAR(255) NOT NULL
);

CREATE TABLE category
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    parent_id  BIGINT   DEFAULT NULL COMMENT '부모 카테고리 ID (자기 참조)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE refund
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    reason     TEXT   NOT NULL,
    status     ENUM('PENDING', 'APPROVED', 'CANCELED') NOT NULL COMMENT '환불 상태',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE coupons
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255)   NOT NULL COMMENT '쿠폰 이름',
    description                VARCHAR(300)   NOT NULL COMMENT '쿠폰 설명',
    discount_type       VARCHAR(20)    NOT NULL COMMENT '쿠폰 종류 (FIXED, PERCENTAGE)',
    discount_value      DECIMAL(19, 2) NOT NULL COMMENT '할인 값 (금액 또는 비율)',
    expiration_type     VARCHAR(30)    NOT NULL COMMENT '유효기한 종류 (VALID_DAYS_ON_ISSUE, FIXED_PERIOD)',
    valid_days          INT COMMENT '유효 일수 (발급일 기준)',
    start_date          TIMESTAMP COMMENT '고정 기간 시작일',
    end_date            TIMESTAMP COMMENT '고정 기간 종료일',
    min_order_amount    DECIMAL(19, 2) DEFAULT 0 COMMENT '최소 주문 금액',
    max_discount_amount DECIMAL(19, 2) COMMENT '최대 할인 금액 (정률 할인용)',
    total_quantity      INT COMMENT '총 발급 가능 수량 (null이면 무제한)',
    issued_quantity     INT            DEFAULT 0 COMMENT '발급된 수량',
    status              VARCHAR(20)    DEFAULT 'ACTIVE' COMMENT '쿠폰 상태 (ACTIVE, INACTIVE)',
    created_at          TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE user_coupons
(
    id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT    NOT NULL COMMENT '사용자 ID',
    coupon_id       BIGINT    NOT NULL COMMENT '쿠폰 템플릿 ID',
    is_used         BOOLEAN   DEFAULT FALSE COMMENT '사용 여부',
    used_at         TIMESTAMP COMMENT '사용 일시',
    issued_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '발급 일시',
    expiration_date TIMESTAMP NOT NULL COMMENT '만료 일시 (발급 시 계산하여 저장)'
);

CREATE TABLE coupon_applicable
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    coupon_id   BIGINT NOT NULL,
    target_id   BIGINT,
    target_type ENUM('PRODUCT', 'CATEGORY') NOT NULL COMMENT '적용 대상 타입'
);
