package com.west.orders.controller;

import com.west.orders.dto.response.CupcakeResponseModel;
import com.west.orders.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@AllArgsConstructor
public class CupcakeController {

    private InventoryService inventoryService;

    @Operation(summary = "Retrieve all cupcake details")
    @GetMapping("/cupcakes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupcakes retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CupcakeResponseModel.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content) })
    public List<CupcakeResponseModel> getCupcakes() {

       return inventoryService.getCupcakes();
    }
}
