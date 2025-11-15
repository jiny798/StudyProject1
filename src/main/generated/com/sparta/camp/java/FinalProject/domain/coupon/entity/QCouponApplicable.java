package com.sparta.camp.java.FinalProject.domain.coupon.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCouponApplicable is a Querydsl query type for CouponApplicable
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCouponApplicable extends EntityPathBase<CouponApplicable> {

    private static final long serialVersionUID = -747321652L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCouponApplicable couponApplicable = new QCouponApplicable("couponApplicable");

    public final QCoupon coupon;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> targetId = createNumber("targetId", Long.class);

    public final EnumPath<com.sparta.camp.java.FinalProject.domain.coupon.entity.type.TargetType> targetType = createEnum("targetType", com.sparta.camp.java.FinalProject.domain.coupon.entity.type.TargetType.class);

    public QCouponApplicable(String variable) {
        this(CouponApplicable.class, forVariable(variable), INITS);
    }

    public QCouponApplicable(Path<? extends CouponApplicable> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCouponApplicable(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCouponApplicable(PathMetadata metadata, PathInits inits) {
        this(CouponApplicable.class, metadata, inits);
    }

    public QCouponApplicable(Class<? extends CouponApplicable> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupon = inits.isInitialized("coupon") ? new QCoupon(forProperty("coupon")) : null;
    }

}

