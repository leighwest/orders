package com.west.orders.repository;

import com.west.orders.entity.Cupcake;
import com.west.orders.entity.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles(profiles = "test")
class CupcakeRepositoryTest {

    @Autowired
    private CupcakeRepository cupcakeRepository;

    @Test
    public void shouldReturn_cupcake_whenFindByProductCode() {

        Image chocolateCupcakeImage = new Image(1L, "CHOC001", "bucketName", "objectKey");

        Cupcake chocolateCupcake = new Cupcake(1L, "CHOC001", Cupcake.Flavour.CHOCOLATE,
                BigDecimal.valueOf(3.50), chocolateCupcakeImage);
        cupcakeRepository.save(chocolateCupcake);

        Cupcake cupcake = cupcakeRepository.findByProductCode(chocolateCupcake.getProductCode());

        assertThat(cupcake.getId()).isEqualTo(1L);
        assertThat(cupcake.getFlavour()).isEqualTo(Cupcake.Flavour.CHOCOLATE);
    }
}