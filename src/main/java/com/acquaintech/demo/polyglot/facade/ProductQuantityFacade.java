package com.acquaintech.demo.polyglot.facade;

import com.acquaintech.demo.polyglot.dto.DeleteProductResponse;
import com.acquaintech.demo.polyglot.dto.ProductRequest;
import com.acquaintech.demo.polyglot.dto.ProductResponse;
import com.acquaintech.demo.polyglot.exception.ProductNotFoundException;
import com.acquaintech.demo.polyglot.exception.QuantityOperationFailedException;
import com.acquaintech.demo.polyglot.product.Product;
import com.acquaintech.demo.polyglot.product.ProductService;
import com.acquaintech.demo.polyglot.quantity.Quantity;
import com.acquaintech.demo.polyglot.quantity.QuantityService;
import com.acquaintech.demo.polyglot.quantity.dto.QuantityRequest;
import com.acquaintech.demo.polyglot.quantity.dto.QuantityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The ONLY class allowed to coordinate ProductService (MongoDB) and
 * QuantityService (MySQL). ProductController talks only to this facade;
 * ProductService and QuantityService never call each other directly.
 * This boundary is a team convention, not enforced by Spring Modulith.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductQuantityFacade {

    private final ProductService productService;
    private final QuantityService quantityService;

    public ProductResponse createProduct(ProductRequest request) {
        Product product = productService.create(Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .attributes(request.getAttributes())
                .build());

        if (request.getQuantity() == null) {
            return toResponse(product, null);
        }

        try {
            Quantity quantity = quantityService.create(product.getId(), request.getQuantity());
            return toResponse(product, quantity);
        } catch (QuantityOperationFailedException ex) {
            log.warn("Compensating: deleting product {} from MongoDB after MySQL quantity write failed", product.getId());
            productService.deleteById(product.getId());
            throw ex;
        }
    }

    public ProductResponse getProduct(String productId) {
        if (!productService.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }
        Product product = productService.getById(productId);
        List<Quantity> quantities = quantityService.findByProductId(productId);
        return toResponseWithQuantities(product, quantities);
    }

    public ProductResponse addQuantity(String productId, QuantityRequest request) {
        if (!productService.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }
        Product product = productService.getById(productId);
        Quantity quantity = quantityService.create(productId, request);
        return toResponse(product, quantity);
    }

    public DeleteProductResponse deleteProduct(String productId) {
        if (!productService.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }

        productService.deleteById(productId);

        try {
            quantityService.deleteByProductId(productId);
            return DeleteProductResponse.builder()
                    .productId(productId)
                    .deleted(true)
                    .build();
        } catch (QuantityOperationFailedException ex) {
            log.warn("Product {} deleted from MongoDB, but MySQL quantity cleanup failed after retries; "
                    + "orphaned quantity row(s) require manual cleanup", productId, ex);
            return DeleteProductResponse.builder()
                    .productId(productId)
                    .deleted(true)
                    .quantityCleanupWarning("Product deleted, but quantity cleanup in MySQL failed after retries: "
                            + ex.getMessage())
                    .build();
        }
    }

    private ProductResponse toResponse(Product product, Quantity quantity) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .attributes(product.getAttributes())
                .quantity(quantity == null ? null : toQuantityResponse(quantity))
                .build();
    }

    private ProductResponse toResponseWithQuantities(Product product, List<Quantity> quantities) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .attributes(product.getAttributes())
                .quantities(quantities.stream().map(this::toQuantityResponse).toList())
                .build();
    }

    private QuantityResponse toQuantityResponse(Quantity quantity) {
        return QuantityResponse.builder()
                .id(quantity.getId())
                .productId(quantity.getProductId())
                .quantity(quantity.getQuantity())
                .warehouseLocation(quantity.getWarehouseLocation())
                .build();
    }
}
