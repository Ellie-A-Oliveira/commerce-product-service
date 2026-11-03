# Product Service

The service owns PostgreSQL-backed products, categories, current prices, and availability.

Start PostgreSQL with `docker compose -f ../infra/docker-compose.yml up -d postgres`, then run
`./mvnw spring-boot:run`. Flyway applies the catalog schema automatically.

Run `./mvnw test` to execute the Testcontainers-backed persistence test. Docker must be available
for the integration test.
