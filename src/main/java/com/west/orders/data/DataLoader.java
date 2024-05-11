package com.west.orders.data;

import com.west.orders.entity.Cupcake;
import com.west.orders.entity.Image;
import com.west.orders.repository.CupcakeRepository;
import com.west.orders.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CupcakeRepository cupcakeRepository;

    @Autowired
    private ImageRepository imageRepository;


    @Override
    public void run(String... args) {

        Image chocolateImage = new Image(1L, "CHOC001", "cupcake-orders-images", "belgian_chocolate.webp");
        Image vanillaImage = new Image(2L, "VAN001", "cupcake-orders-images", "classic_vanilla.webp");
        Image lemonImage = new Image(3L, "LEM001", "cupcake-orders-images", "lemon_delight.webp");

        imageRepository.save(chocolateImage);
        imageRepository.save(vanillaImage);
        imageRepository.save(lemonImage);

        Cupcake chocolateCupcake = new Cupcake(1L, "CHOC001", Cupcake.Flavour.CHOCOLATE, BigDecimal.valueOf(3.50), chocolateImage);
        Cupcake vanillaCupcake = new Cupcake(2L, "VAN001", Cupcake.Flavour.VANILLA,  BigDecimal.valueOf(3.50), vanillaImage);
        Cupcake lemonCupcake = new Cupcake(3L, "LEM001", Cupcake.Flavour.LEMON,  BigDecimal.valueOf(3.50), lemonImage);

        cupcakeRepository.save(chocolateCupcake);
        cupcakeRepository.save(vanillaCupcake);
        cupcakeRepository.save(lemonCupcake);
    }
}
