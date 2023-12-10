package com.west.orders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
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
    @Enumerated(EnumType.STRING)
    private Flavour flavour;

}
