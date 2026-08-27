package com.acquaintech.demo.polyglot.dto;

import com.acquaintech.demo.polyglot.product.Attribute;
import com.acquaintech.demo.polyglot.quantity.dto.QuantityResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String id;
    private String sku;
    private String name;
    private String description;
    private double price;
    private List<Attribute> attributes;

    /**
     * Populated only when a quantity row was actually persisted for this
     * product (either at creation time or via the register-quantity endpoint).
     */
    private QuantityResponse quantity;
}
