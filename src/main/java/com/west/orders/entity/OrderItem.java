package com.west.orders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private String productCode;

    @ManyToOne
    @JoinColumn(name = "cupcake_id", nullable = false)
    private Cupcake cupcake;

    @Column(nullable = false)
    private int count;

    @Column(nullable = false)
    private BigDecimal unitPrice;
}
