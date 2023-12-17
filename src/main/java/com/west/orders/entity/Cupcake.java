package com.west.orders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        STRAWBERRY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String productCode;

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private Flavour flavour;

}
