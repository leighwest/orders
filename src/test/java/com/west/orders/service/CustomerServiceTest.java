package com.west.orders.service;

import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.entity.Customer;
import com.west.orders.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.west.orders.TestUtils.createCustomer;
import static com.west.orders.TestUtils.createInitialOrderRequestModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    public void shouldReturn_existingCustomer_ifCustomerExists() {

        InitialOrderRequestModel customerOrder = createInitialOrderRequestModel("VAN001");

        Customer existingCustomer = createCustomer();

        when(customerRepository.findByEmail(anyString())).thenReturn(existingCustomer);

        Customer customer = customerService.findOrSaveCustomer(customerOrder);

        assertThat(customer).isNotNull();
        assertThat(customer.getEmail()).isEqualTo(existingCustomer.getEmail());
        assertThat(customer.getFirstName()).isEqualTo(existingCustomer.getFirstName());
        assertThat(customer.getSurname()).isEqualTo(existingCustomer.getSurname());

        verify(customerRepository, times(1)).findByEmail(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    public void shouldSave_andReturnCustomer_ifCustomer_doesNotExist() {

        InitialOrderRequestModel customerOrder = createInitialOrderRequestModel("VAN001");

        Customer expectedCustomer = createCustomer();

        when(customerRepository.findByEmail(anyString())).thenReturn(null);
        when(customerRepository.save(any(Customer.class))).thenReturn(expectedCustomer);

        Customer customer = customerService.findOrSaveCustomer(customerOrder);

        assertThat(customer).isNotNull();
        assertThat(customer.getEmail()).isEqualTo(expectedCustomer.getEmail());
        assertThat(customer.getFirstName()).isEqualTo(expectedCustomer.getFirstName());
        assertThat(customer.getSurname()).isEqualTo(expectedCustomer.getSurname());

        verify(customerRepository, times(1)).findByEmail(anyString());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}