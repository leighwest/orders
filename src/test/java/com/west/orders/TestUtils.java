package com.west.orders;

import com.west.orders.dto.AddressDto;
import com.west.orders.dto.CustomerDto;
import com.west.orders.dto.OrderItemDto;
import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.entity.Address;
import com.west.orders.entity.Customer;

import java.util.List;

public class TestUtils {

    public static Customer createCustomer() {
        return Customer.builder()
                .firstName("Fred")
                .surname("Flintstone")
                .email("fred.flinstone@example.com")
                .build();
    }

    public static Address createAddress() {
        return Address.builder()
                .streetNumber("17")
                .streetName("Example")
                .streetType("Avenue")
                .suburb("Exampleton")
                .postCode("3000")
                .state(Address.State.VIC)
                .build();
    }

    public static InitialOrderRequestModel createInitialOrderRequestModel(String productCode) {
        return InitialOrderRequestModel.builder()
                .customer(CustomerDto.builder()
                        .firstName("Fred")
                        .surname("Flintstone")
                        .email("fred.flindstone@exampele.com")
                        .build())
                .address(AddressDto.builder()
                        .streetNumber("17")
                        .streetName("Example")
                        .streetType("Avenue")
                        .suburb("Exampleton")
                        .postCode("3000")
                        .state(Address.State.VIC)
                        .build())
                .cupcakes(List.of(OrderItemDto.builder()
                                .productCode("CHOC001")
                                .count(5)
                                .build(),
                        OrderItemDto.builder()
                                .productCode(productCode)
                                .count(3)
                                .build()
                ))
                .build();
    }
}
