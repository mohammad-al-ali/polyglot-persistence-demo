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

    @Valid
    private List<Attribute> attributes;
    @Valid
    private QuantityRequest quantity;
}
