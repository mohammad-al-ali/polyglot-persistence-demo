package com.acquaintech.demo.polyglot.quantity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuantityRepository extends JpaRepository<Quantity, Long> {

    List<Quantity> findByProductId(String productId);

    // Derived delete queries execute as find-then-remove under the hood, so
    // Spring Data requires a transaction around the call — without this the
    // repository throws TransactionRequiredException at runtime.
    @Transactional
    void deleteByProductId(String productId);
}
