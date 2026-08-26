# Polyglot Persistence Demo

Simplified Spring Boot app (no Spring Modulith) demonstrating polyglot persistence:
product catalog in **MongoDB**, quantity/inventory in **MySQL**, coordinated
through a single Facade layer.

## Boundary rules (team convention — not tool-enforced)

- `ProductController` talks only to `ProductQuantityFacade` — it never calls
  `ProductService` or `QuantityService` directly.
- `ProductService` and `QuantityService` never call each other directly — all
  cross-store coordination goes exclusively through `ProductQuantityFacade`.
- Because Spring Modulith is intentionally not used here, this separation is a
  team agreement, not something enforced automatically by the build. In a
  real project you'd either adopt Modulith (`ApplicationModules.verify()`) or
  an ArchUnit rule to make the boundary a compile/test-time guarantee.

## Running locally

Requires a local MongoDB (`localhost:27017`) and MySQL (`localhost:3306`,
database `polyglot_demo_mysql`) instance. Connection settings are in
`src/main/resources/application.properties`.

```bash
mvn spring-boot:run
```

The app starts on port `8081`.

## Endpoints

| Method | Path                          | Purpose                                                                 |
|--------|-------------------------------|--------------------------------------------------------------------------|
| POST   | `/api/products`               | Create a product; `quantity` field is optional (dual write when present) |
| POST   | `/api/products/{id}/quantity` | Register quantity for an existing product                                |
| DELETE | `/api/products/{id}`          | Delete a product and clean up its quantity row(s)                        |

### Create product (no quantity)

```json
POST /api/products
{
  "sku": "SKU-001",
  "name": "Widget",
  "description": "A basic widget",
  "price": 9.99
}
```

### Create product + quantity together

```json
POST /api/products
{
  "sku": "SKU-002",
  "name": "Gadget",
  "description": "A fancy gadget",
  "price": 19.99,
  "quantity": {
    "quantityOnHand": 100,
    "warehouseLocation": "WH-1"
  }
}
```

If the MySQL write keeps failing after 3 retry attempts, the facade deletes
the Mongo product it just created (compensating action) and returns a
`502 Bad Gateway` `ProblemDetail` — there is no distributed transaction across
the two stores, so this manual rollback is how consistency is restored.

### Register quantity for an existing product

```json
POST /api/products/{productId}/quantity
{
  "quantityOnHand": 50,
  "warehouseLocation": "WH-2"
}
```

Returns `404` if `productId` doesn't exist in MongoDB — this check is a
manual existence lookup, not a database-enforced foreign key, since MySQL
cannot see into MongoDB.

### Delete product

```
DELETE /api/products/{productId}
```

Deletes the Mongo product first, then cleans up MySQL quantity rows (same
retry policy). If cleanup keeps failing, the response still reports the
product as deleted but includes a `quantityCleanupWarning` noting the
orphaned row(s) need manual/eventual cleanup — again illustrating the lack of
automatic cross-store referential integrity.
