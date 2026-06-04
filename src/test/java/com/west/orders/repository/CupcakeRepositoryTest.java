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

    @Autowired
    private ImageRepository imageRepository;

    @Test
    public void shouldReturn_cupcake_whenFindByProductCode() {

        Image savedImage = imageRepository.save(new Image(null, "CHOC001", "bucketName", "objectKey"));
        Cupcake chocolateCupcake = new Cupcake(null, "CHOC001", Cupcake.Flavour.CHOCOLATE,
                BigDecimal.valueOf(3.50), savedImage);
        Cupcake savedCupcake = cupcakeRepository.save(chocolateCupcake);

        Cupcake found = cupcakeRepository.findByProductCode(savedCupcake.getProductCode());

        assertThat(found.getId()).isEqualTo(savedCupcake.getId());
        assertThat(found.getFlavour()).isEqualTo(Cupcake.Flavour.CHOCOLATE);
    }
}