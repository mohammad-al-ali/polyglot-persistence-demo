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

    /**
     * Non-null only when the product was deleted from MongoDB but the
     * MySQL quantity cleanup failed after retries — the orphaned row(s)
     * need manual/eventual cleanup. Demonstrates the lack of automatic
     * cross-store referential integrity in this polyglot setup.
     */
    private String quantityCleanupWarning;
}
