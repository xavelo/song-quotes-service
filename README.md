# Song Quotes Service

[![CI/CD Pipeline](https://github.com/xavelo/song-quotes-service/actions/workflows/ci.yaml/badge.svg)](https://github.com/xavelo/song-quotes-service/actions/workflows/ci.yaml)
[![Coverage](.github/badges/jacoco.svg)](https://github.com/xavelo/song-quotes-service/actions/workflows/ci.yaml)
[![Docker Pulls](https://img.shields.io/docker/pulls/xavelo/song-quotes-service)](https://hub.docker.com/r/xavelo/song-quotes-service)
[![Docker Image Version](https://img.shields.io/docker/v/xavelo/song-quotes-service?sort=semver)](https://hub.docker.com/r/xavelo/song-quotes-service/tags)

## Overview

Song Quotes Service is a Spring Boot application that exposes a REST API for
browsing and managing memorable song quotes. Quotes are stored in MySQL, are
served through a clean Hexagonal architecture, and integrate with Spotify to
fetch artist metadata and top tracks. Kafka is used to publish events whenever a
new quote is created, while Micrometer provides application metrics that can be
scraped by Prometheus for observability.【F:src/main/java/com/xavelo/sqs/adapter/out/kafka/QuoteCreatedKafkaAdapter.java†L1-L68】【F:src/main/java/com/xavelo/sqs/adapter/out/metrics/MicrometerMetricsAdapter.java†L1-L61】【F:src/main/java/com/xavelo/sqs/adapter/out/spotify/SpotifyAdapter.java†L1-L124】

## Architecture highlights

* **Hexagonal ports & adapters** – Core use cases and domain models live in the
  `application` and `port` packages, while infrastructure concerns such as MySQL
  persistence, Kafka, metrics, and Spotify live in dedicated adapters.【F:src/main/java/com/xavelo/sqs/application/service/QuoteService.java†L1-L145】【F:src/main/java/com/xavelo/sqs/port/in/GetQuotesUseCase.java†L1-L13】
* **Secure-by-default** – The service is configured as an OAuth2 resource server
  that expects JWT bearer tokens. Public quote and artist APIs are open, while
  authenticated scopes can be applied to protected endpoints such as `/api/secure`.【F:src/main/java/com/xavelo/sqs/configuration/SecurityConfig.java†L1-L24】【F:src/main/java/com/xavelo/sqs/adapter/in/http/secure/SecurePingController.java†L1-L32】
* **Database migrations** – Flyway migrations version and evolve the MySQL
  schema, ensuring reproducible environments.【F:src/main/resources/db/migration/V1__create_quotes_table.sql†L1-L49】【F:src/main/resources/db/migration/V2__add_spotify_artist_metadata.sql†L1-L36】

## Prerequisites

* Java 17+
* Maven 3.9+ (or the included `mvnw` wrapper)
* Access to a MySQL 8 database
* Access to a Kafka broker (for quote-created events)
* Spotify API credentials (client id/secret) for metadata enrichment

## Running the application locally

1. **Install dependencies**
   ```bash
   ./mvnw dependency:go-offline
   ```
2. **Configure environment** – Update `src/main/resources/application.yaml` or
   supply overrides via environment variables / JVM system properties for your
   MySQL, Kafka, and Spotify credentials (see [Configuration](#configuration)).
3. **Run database migrations and start the app**
   ```bash
   ./mvnw spring-boot:run
   ```
4. **Package an executable jar**
   ```bash
   ./mvnw clean package
   java -jar target/song-quotes-service-*.jar
   ```

## Docker

Build a production-ready image after packaging the jar:

```bash
./mvnw clean package
docker build -t song-quotes-service:local .
docker run -p 8080:8080 song-quotes-service:local
```

The container image expects the same environment variables described below to be
available at runtime.

## Configuration

The default configuration is defined in `application.yaml` and can be overridden
per environment.【F:src/main/resources/application.yaml†L1-L38】

| Property | Description |
| --- | --- |
| `spring.datasource.*` | MySQL connection details for the `song-quotes` schema. |
| `spring.kafka.bootstrap-servers` | Kafka bootstrap server for publishing quote events. |
| `spring.security.oauth2.resourceserver.jwt.*` | OAuth2 issuer/JWK URLs for JWT validation. |
| `spotify.api.clientId` / `spotify.api.clientSecret` | Spotify application credentials used when enriching artist metadata. |
| `management.endpoints.web.exposure.include` | Exposes health, info, metrics, and Prometheus actuator endpoints. |

Set the Spotify secrets via environment variables:

```bash
export SPOTIFY_CLIENT_ID=<your-client-id>
export SPOTIFY_CLIENT_SECRET=<your-client-secret>
```

## Database schema

Flyway manages database structure. `V1__create_quotes_table.sql` creates the core
`quote` table and aggregates, while `V2__add_spotify_artist_metadata.sql` stores
Spotify artist metadata used by the enrichment adapter.【F:src/main/resources/db/migration/V1__create_quotes_table.sql†L1-L49】【F:src/main/resources/db/migration/V2__add_spotify_artist_metadata.sql†L1-L36】

## REST API

All endpoints default to JSON and are served on port `8080`.

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/ping` | Liveness probe returning the serving pod/host name.【F:src/main/java/com/xavelo/sqs/adapter/in/http/ping/PingController.java†L17-L35】 |
| `GET` | `/api/quote/random` | Returns a random quote, or `404` when no quotes exist.【F:src/main/java/com/xavelo/sqs/adapter/in/http/quote/QuoteController.java†L36-L44】 |
| `GET` | `/api/quote/{id}` | Fetch a single quote by id.【F:src/main/java/com/xavelo/sqs/adapter/in/http/quote/QuoteController.java†L46-L52】 |
| `GET` | `/api/quotes` | Retrieve all quotes.【F:src/main/java/com/xavelo/sqs/adapter/in/http/quote/QuoteController.java†L28-L34】 |
| `GET` | `/api/quotes/count` | Count all quotes in storage.【F:src/main/java/com/xavelo/sqs/adapter/in/http/quote/QuoteController.java†L30-L41】 |
| `GET` | `/api/quotes/top10` | Return the ten most-viewed quotes.【F:src/main/java/com/xavelo/sqs/adapter/in/http/quote/QuoteController.java†L54-L59】 |
| `GET` | `/api/artist/{id}` | Fetch a single artist enriched with Spotify metadata.【F:src/main/java/com/xavelo/sqs/adapter/in/http/artist/ArtistController.java†L25-L32】 |
| `GET` | `/api/artists` | List artists with the number of quotes available for each.【F:src/main/java/com/xavelo/sqs/adapter/in/http/artist/ArtistController.java†L34-L38】 |
| `GET` | `/api/secure/ping` | Ping endpoint requiring a valid JWT, useful for smoke-testing auth flows.【F:src/main/java/com/xavelo/sqs/adapter/in/http/secure/SecurePingController.java†L14-L26】 |
| `POST` | `/admin/quote` | Create a single quote and return the new id.【F:src/main/java/com/xavelo/sqs/adapter/in/http/admin/AdminController.java†L29-L35】 |
| `POST` | `/admin/quotes` | Bulk-create quotes and return generated ids.【F:src/main/java/com/xavelo/sqs/adapter/in/http/admin/AdminController.java†L37-L42】 |
| `PUT` | `/admin/quote/{id}` | Replace a quote (excluding restricted counters).【F:src/main/java/com/xavelo/sqs/adapter/in/http/admin/AdminController.java†L44-L62】 |
| `PATCH` | `/admin/quote/{id}` | Partially update a quote (excluding restricted counters).【F:src/main/java/com/xavelo/sqs/adapter/in/http/admin/AdminController.java†L66-L74】 |
| `DELETE` | `/admin/quote/{id}` | Delete a quote by id.【F:src/main/java/com/xavelo/sqs/adapter/in/http/admin/AdminController.java†L39-L43】 |
| `GET` | `/admin/export` | Export all quotes as SQL insert statements for backup or migration purposes.【F:src/main/java/com/xavelo/sqs/adapter/in/http/admin/AdminController.java†L76-L80】 |

## Observability

Micrometer metrics are emitted via Spring Boot Actuator. With the provided
configuration, Prometheus can scrape `/actuator/prometheus` and standard health
information is available under `/actuator/health` and `/actuator/info`.【F:src/main/resources/application.yaml†L28-L38】【F:src/main/java/com/xavelo/sqs/adapter/out/metrics/MicrometerMetricsAdapter.java†L1-L61】

## Testing & quality

Run the full verification suite (unit tests, integration tests, and coverage)
with Maven:

```bash
./mvnw clean verify
```

JaCoCo reports are written to `target/site/jacoco/index.html` and can be opened
in a browser for local inspection.
