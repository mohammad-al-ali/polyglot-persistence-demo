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
| GET    | `/api/products/{id}`          | Fetch a product with its full attribute list and *all* quantity rows     |
| POST   | `/api/products/{id}/quantity` | Register quantity for an existing product                                |
| DELETE | `/api/products/{id}`          | Delete a product and clean up its quantity row(s)                        |

### Create product (no quantity)

```json
POST /api/products
{
  "sku": "SKU-001",
  "name": "Widget",
  "description": "A basic widget",
  "price": 9.99,
  "attributes": [
    { "name": "Color", "value": "Red" },
    { "name": "Material", "value": "Plastic" }
  ]
}
```

`attributes` is optional — a product can have zero, one, or many name/value
traits. They're stored as nested documents inside the product in MongoDB, so
a product with several values for the same trait (e.g. two colors) just has
two `Attribute` entries with the same `name`.

### Create product + quantity together

```json
POST /api/products
{
  "sku": "SKU-002",
  "name": "Gadget",
  "description": "A fancy gadget",
  "price": 19.99,
  "attributes": [
    { "name": "Size", "value": "M" }
  ],
  "quantity": {
    "quantity": 100,
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
  "quantity": 50,
  "warehouseLocation": "WH-2"
}
```

Returns `404` if `productId` doesn't exist in MongoDB — this check is a
manual existence lookup, not a database-enforced foreign key, since MySQL
cannot see into MongoDB. Calling this endpoint again for the same product
adds another row rather than replacing the existing one — a product can have
more than one quantity row (e.g. one per warehouse).

### Get a product

```
GET /api/products/{productId}
```

Returns the product with its `attributes` and every quantity row currently
in MySQL for it, under `quantities`:

```json
{
  "id": "6a90230155b8e0ce8c10a7a2",
  "sku": "SKU-002",
  "name": "Gadget",
  "description": "A fancy gadget",
  "price": 19.99,
  "attributes": [ { "name": "Size", "value": "M" } ],
  "quantity": null,
  "quantities": [
    { "id": 5, "productId": "6a90230155b8e0ce8c10a7a2", "quantity": 100, "warehouseLocation": "WH-1" },
    { "id": 6, "productId": "6a90230155b8e0ce8c10a7a2", "quantity": 20, "warehouseLocation": "WH-2" }
  ]
}
```

Note the split between `quantity` and `quantities`: the write endpoints
(`POST /api/products`, `POST /api/products/{id}/quantity`) return `quantity`
— the single row that call just touched — and leave `quantities` null. This
endpoint does the opposite: it returns the full, authoritative list under
`quantities` and leaves `quantity` null. Returns `404` if the product doesn't
exist in MongoDB.

### Delete product

```
DELETE /api/products/{productId}
```

Deletes the Mongo product first, then cleans up MySQL quantity rows (same
retry policy). If cleanup keeps failing, the response still reports the
product as deleted but includes a `quantityCleanupWarning` noting the
orphaned row(s) need manual/eventual cleanup — again illustrating the lack of
automatic cross-store referential integrity.
