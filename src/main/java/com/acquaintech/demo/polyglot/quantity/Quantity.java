package com.acquaintech.demo.polyglot.quantity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MySQL-side quantity row. productId is a plain string reference to a Mongo
 * Product document — there is intentionally no foreign key, since MySQL
 * cannot enforce referential integrity against a different database engine.
 */
@Entity
@Table(name = "quantities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quantity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private int quantityOnHand;

    private String warehouseLocation;
}
