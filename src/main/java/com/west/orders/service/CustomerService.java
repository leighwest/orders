package com.west.orders.service;

import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.entity.Customer;
import com.west.orders.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CustomerService {

    private CustomerRepository customerRepository;

    public Customer findOrSaveCustomer(InitialOrderRequestModel customerOrder) {

//        TODO: validation
//        Map<ValidationContextDataType, Object> map = Map.of(
//                ValidationContextDataType.PRODUCT_CODES, productCodes,
//                ValidationContextDataType.ORDER_ITEMS, customerOrder.getCupcakes()
//        );
//
//        validationProcessor.validate(new ValidationContext(map));

        Customer existingCustomer = customerRepository.findByEmail(customerOrder.getCustomer().getEmail());

        if (existingCustomer != null) {
            return existingCustomer;
        } else {
            Customer customer = Customer.builder()
                    .firstName(customerOrder.getCustomer().getFirstName())
                    .surname(customerOrder.getCustomer().getSurname())
                    .email(customerOrder.getCustomer().getEmail())
                    .build();
            return customerRepository.save(customer);
        }
    }
}
