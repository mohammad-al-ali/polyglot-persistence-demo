package com.acquaintech.demo.polyglot.quantity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityRequest {

    @NotNull
    @Min(0)
    private Integer quantity;

    private String warehouseLocation;
}
