package com.acquaintech.demo.polyglot.quantity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuantityRepository extends JpaRepository<Quantity, Long> {

    List<Quantity> findByProductId(String productId);

    void deleteByProductId(String productId);
}
