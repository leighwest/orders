package com.west.orders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "cupcakes")
public class Cupcake {

    public enum Flavour {
        CHOCOLATE,
        VANILLA,
        LEMON
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String productCode;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private Flavour flavour;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @OneToOne
    @JoinColumn(name = "image", referencedColumnName = "productCode")
    private Image image;
}
