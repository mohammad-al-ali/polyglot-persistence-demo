package com.acquaintech.demo.polyglot.quantity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuantityRepository extends JpaRepository<Quantity, Long> {

    List<Quantity> findByProductId(String productId);

    @Transactional
    void deleteByProductId(String productId);
}
