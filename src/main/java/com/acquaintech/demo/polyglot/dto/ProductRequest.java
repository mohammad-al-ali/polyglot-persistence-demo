package com.acquaintech.demo.polyglot.dto;

import com.acquaintech.demo.polyglot.product.Attribute;
import com.acquaintech.demo.polyglot.quantity.dto.QuantityRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank
    private String sku;

    @NotBlank
    private String name;

    private String description;

    private double price;

    /**
     * Optional. One or more name/value traits (e.g. Color=Red, Size=M).
     */
    @Valid
    private List<Attribute> attributes;

    /**
     * Optional. When present, the product and its quantity are created
     * together (dual write with retry + compensation). When absent, only
     * the product is written to MongoDB.
     */
    @Valid
    private QuantityRequest quantity;
}
