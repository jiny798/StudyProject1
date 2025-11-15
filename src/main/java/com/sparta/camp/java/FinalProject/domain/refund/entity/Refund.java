package com.sparta.camp.java.FinalProject.domain.refund.entity;

import com.sparta.camp.java.FinalProject.domain.order.entity.Order;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refund")
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Refund create(Order order, User user, String reason, RefundStatus status) {
        Refund refund = new Refund();
        refund.order = order;
        refund.user = user;
        refund.reason = reason;
        refund.status = status;
        return refund;
    }
}