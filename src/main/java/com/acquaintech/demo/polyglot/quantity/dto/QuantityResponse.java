package com.acquaintech.demo.polyglot.quantity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuantityResponse {

    private Long id;
    private String productId;
    private int quantity;
    private String warehouseLocation;
}
