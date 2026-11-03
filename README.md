# Product Service

The service owns PostgreSQL-backed products, categories, current prices, and availability.

Start PostgreSQL with `docker compose -f ../infra/docker-compose.yml up -d postgres`, then run
`./mvnw spring-boot:run`. Flyway applies the catalog schema automatically.

Run `./mvnw test` to execute the Testcontainers-backed persistence test. Docker must be available
for the integration test.

## Product API

- `POST /products` creates an active product and returns `201 Created`.
- `GET /products` lists active products only.
- `GET /products/{id}` returns an active product or `404 Not Found`.
- `PUT /products/{id}` replaces mutable product fields and returns `200 OK`.
- `DELETE /products/{id}` deactivates a product and returns `204 No Content`.
- Invalid request data returns `400 Bad Request`; unknown categories also return `400`, and duplicate product IDs return `409 Conflict`.

Create and update requests contain `id`, `name`, `description`, non-negative `priceInCents`,
non-negative `availableStock`, `available`, and optional existing `categoryIds`.
