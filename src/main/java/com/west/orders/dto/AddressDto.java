package com.west.orders.dto;

import com.west.orders.entity.Address.State;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Schema(description = "The definition of an Address")
public class AddressDto {

    @Schema(description = "Apartment or unit number", example = "1B")
    private String unitNumber;

    @Schema(description = "Street number", example = "123")
    private String streetNumber;

    @Schema(description = "Street name", example = "Example")
    private String streetName;

    @Schema(description = "Street type", example = "Ave")
    private String streetType;

    @Schema(description = "Suburb", example = "Exampleton")
    private String suburb;

    @Schema(description = "Postcode", example = "3000")
    private String postCode;

    @Schema(description = "State or territory", implementation = State.class)
    private State state;
}
