package com.west.orders.repository;

import com.west.orders.entity.Cupcake;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CupcakeRepository extends JpaRepository<Cupcake, Long> {
}
