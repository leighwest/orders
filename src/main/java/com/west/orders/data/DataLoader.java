package com.west.orders.data;

import com.west.orders.entity.Cupcake;
import com.west.orders.repository.CupcakeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CupcakeRepository cupcakeRepository;


    @Override
    public void run(String... args) throws Exception {

        Cupcake chocolateCupcake = new Cupcake(1L, Cupcake.Flavour.CHOCOLATE);
        Cupcake vanillaCupcake = new Cupcake(2L, Cupcake.Flavour.VANILLA);
        Cupcake strawberryCupcake = new Cupcake(3L, Cupcake.Flavour.STRAWBERRY);

        cupcakeRepository.save(chocolateCupcake);
        cupcakeRepository.save(vanillaCupcake);
        cupcakeRepository.save(strawberryCupcake);

    }
}
