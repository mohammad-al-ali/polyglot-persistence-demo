package com.acquaintech.demo.polyglot.controller;

import com.acquaintech.demo.polyglot.dto.DeleteProductResponse;
import com.acquaintech.demo.polyglot.dto.ProductRequest;
import com.acquaintech.demo.polyglot.dto.ProductResponse;
import com.acquaintech.demo.polyglot.facade.ProductQuantityFacade;
import com.acquaintech.demo.polyglot.quantity.dto.QuantityRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Talks ONLY to ProductQuantityFacade — never to ProductService or
 * QuantityService directly. This boundary is a team convention, not
 * enforced by Spring Modulith (intentionally not used in this demo).
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductQuantityFacade productQuantityFacade;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productQuantityFacade.createProduct(request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String productId) {
        return ResponseEntity.ok(productQuantityFacade.getProduct(productId));
    }

    @PostMapping("/{productId}/quantity")
    public ResponseEntity<ProductResponse> addQuantity(@PathVariable String productId,
                                                         @Valid @RequestBody QuantityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productQuantityFacade.addQuantity(productId, request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<DeleteProductResponse> deleteProduct(@PathVariable String productId) {
        return ResponseEntity.ok(productQuantityFacade.deleteProduct(productId));
    }
}
