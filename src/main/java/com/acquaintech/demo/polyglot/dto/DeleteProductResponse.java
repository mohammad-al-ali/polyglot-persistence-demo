package com.acquaintech.demo.polyglot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteProductResponse {

    private String productId;
    private boolean deleted;
    private String quantityCleanupWarning;
}
