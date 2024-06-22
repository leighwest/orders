package com.west.orders.repository;

import com.west.orders.entity.Cupcake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CupcakeRepository extends JpaRepository<Cupcake, String> {

    Cupcake findByProductCode(String productCode);

    @Query("SELECT c.productCode FROM Cupcake c ORDER BY c.productCode ASC")
    List<String> findAllProductCodes();
}
