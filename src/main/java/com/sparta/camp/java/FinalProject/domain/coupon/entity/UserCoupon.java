package com.sparta.camp.java.FinalProject.domain.coupon.entity;

import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_coupons")
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(nullable = false)
    private boolean isUsed = false;

    private LocalDateTime usedAt;
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Builder
    public UserCoupon(User user, Coupon coupon, boolean isUsed, LocalDateTime usedAt, LocalDateTime issuedAt, LocalDateTime expirationDate) {
        this.user = user;
        this.coupon = coupon;
        this.isUsed = isUsed;
        this.usedAt = usedAt;
        this.issuedAt = issuedAt;
        this.expirationDate = expirationDate;
    }

    public void use() {
        this.isUsed = true;
    }
}