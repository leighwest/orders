package com.west.orders.repository;

import com.west.orders.TestUtils;
import com.west.orders.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles(profiles = "test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void shouldReturn_customer_whenFindByEmail() {

        Customer expectedCustomer = TestUtils.createCustomer();

        customerRepository.save(expectedCustomer);

        Customer customer = customerRepository.findByEmail(expectedCustomer.getEmail());

        assertThat(customer).isNotNull();
        assertThat(customer.getEmail()).isEqualTo(expectedCustomer.getEmail());
        assertThat(customer.getFirstName()).isEqualTo(expectedCustomer.getFirstName());
        assertThat(customer.getSurname()).isEqualTo(expectedCustomer.getSurname());
    }
}