package com.west.orders.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Schema(description = "The definition of a Customer")
public class CustomerDto {

    @Schema(description = "First (given) name of customer")
    private String firstName;

    @Schema(description = "Surname or family name of customer")
    private String surname;

    @Schema(description = "Email address of customer")
    private String email;
}
