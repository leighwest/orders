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
@Table(name = "address")
public class Address {

    public enum State {
        VIC, NSW, QLD, WA, SA, TAS, ACT, NT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String unitNumber;

    @Column(nullable = false)
    private String streetNumber;

    @Column(nullable = false)
    private String streetName;

    @Column(nullable = false)
    private String streetType;

    @Column(nullable = false)
    private String suburb;

    @Column(nullable = false)
    private String postCode;

    @Enumerated(EnumType.STRING)
    private State state;
}
