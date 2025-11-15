package com.sparta.camp.java.FinalProject.domain.product.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductImages is a Querydsl query type for ProductImages
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductImages extends EntityPathBase<ProductImages> {

    private static final long serialVersionUID = -450968737L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductImages productImages = new QProductImages("productImages");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final QProduct product;

    public QProductImages(String variable) {
        this(ProductImages.class, forVariable(variable), INITS);
    }

    public QProductImages(Path<? extends ProductImages> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductImages(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductImages(PathMetadata metadata, PathInits inits) {
        this(ProductImages.class, metadata, inits);
    }

    public QProductImages(Class<? extends ProductImages> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

