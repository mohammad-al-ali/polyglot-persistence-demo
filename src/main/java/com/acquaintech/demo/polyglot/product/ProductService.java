package com.acquaintech.demo.polyglot.product;

import com.acquaintech.demo.polyglot.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Mongo-only logic for the product catalog.
 * Must never call QuantityService directly — see ProductQuantityFacade.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product getById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public boolean existsById(String productId) {
        return productRepository.existsById(productId);
    }

    public void deleteById(String productId) {
        productRepository.deleteById(productId);
    }
}
