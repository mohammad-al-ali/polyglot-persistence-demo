package com.acquaintech.demo.polyglot.product;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {

    @NotBlank
    private String name;

    @NotBlank
    private String value;
}
