package com.sparta.camp.java.FinalProject.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1597156905L;

    public static final QUser user = new QUser("user");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<com.sparta.camp.java.FinalProject.domain.order.entity.Order, com.sparta.camp.java.FinalProject.domain.order.entity.QOrder> orders = this.<com.sparta.camp.java.FinalProject.domain.order.entity.Order, com.sparta.camp.java.FinalProject.domain.order.entity.QOrder>createList("orders", com.sparta.camp.java.FinalProject.domain.order.entity.Order.class, com.sparta.camp.java.FinalProject.domain.order.entity.QOrder.class, PathInits.DIRECT2);

    public final StringPath passwordHash = createString("passwordHash");

    public final StringPath phone = createString("phone");

    public final ListPath<com.sparta.camp.java.FinalProject.domain.product.entity.Product, com.sparta.camp.java.FinalProject.domain.product.entity.QProduct> products = this.<com.sparta.camp.java.FinalProject.domain.product.entity.Product, com.sparta.camp.java.FinalProject.domain.product.entity.QProduct>createList("products", com.sparta.camp.java.FinalProject.domain.product.entity.Product.class, com.sparta.camp.java.FinalProject.domain.product.entity.QProduct.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final SetPath<UserRole, QUserRole> userRoles = this.<UserRole, QUserRole>createSet("userRoles", UserRole.class, QUserRole.class, PathInits.DIRECT2);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

