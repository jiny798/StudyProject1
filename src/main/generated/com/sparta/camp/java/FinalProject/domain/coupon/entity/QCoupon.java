package com.sparta.camp.java.FinalProject.domain.coupon.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCoupon is a Querydsl query type for Coupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCoupon extends EntityPathBase<Coupon> {

    private static final long serialVersionUID = -1523690675L;

    public static final QCoupon coupon = new QCoupon("coupon");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final EnumPath<com.sparta.camp.java.FinalProject.domain.coupon.entity.type.DiscountType> discountType = createEnum("discountType", com.sparta.camp.java.FinalProject.domain.coupon.entity.type.DiscountType.class);

    public final NumberPath<java.math.BigDecimal> discountValue = createNumber("discountValue", java.math.BigDecimal.class);

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final EnumPath<com.sparta.camp.java.FinalProject.domain.coupon.entity.type.ExpirationType> expirationType = createEnum("expirationType", com.sparta.camp.java.FinalProject.domain.coupon.entity.type.ExpirationType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> issuedQuantity = createNumber("issuedQuantity", Integer.class);

    public final NumberPath<java.math.BigDecimal> maxDiscountAmount = createNumber("maxDiscountAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> minOrderAmount = createNumber("minOrderAmount", java.math.BigDecimal.class);

    public final StringPath name = createString("name");

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final EnumPath<CouponStatus> status = createEnum("status", CouponStatus.class);

    public final NumberPath<Integer> totalQuantity = createNumber("totalQuantity", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> validDays = createNumber("validDays", Integer.class);

    public QCoupon(String variable) {
        super(Coupon.class, forVariable(variable));
    }

    public QCoupon(Path<? extends Coupon> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCoupon(PathMetadata metadata) {
        super(Coupon.class, metadata);
    }

}

