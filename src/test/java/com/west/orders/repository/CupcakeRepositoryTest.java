package com.west.orders.repository;

import com.west.orders.entity.Cupcake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles(profiles = "test")
class CupcakeRepositoryTest {

    @Autowired
    private CupcakeRepository cupcakeRepository;

    @Test
    public void shouldReturn_cupcake_whenFindByProductCode() {

        Cupcake chocolateCupcake = new Cupcake(1L, "CHOC001", Cupcake.Flavour.CHOCOLATE);
        cupcakeRepository.save(chocolateCupcake);

        Cupcake cupcake = cupcakeRepository.findByProductCode(chocolateCupcake.getProductCode());

        assertThat(cupcake.getId()).isEqualTo(1L);
        assertThat(cupcake.getFlavour()).isEqualTo(Cupcake.Flavour.CHOCOLATE);
    }
}