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
scraped by Prometheus for observability.

## Architecture highlights

* **Hexagonal ports & adapters** – Core use cases and domain models live in the
  `application` and `port` packages, while infrastructure concerns such as MySQL
  persistence, Kafka, metrics, and Spotify live in dedicated adapters.
* **Secure-by-default** – The service is configured as an OAuth2 resource server
  that expects JWT bearer tokens. Public quote and artist APIs are open, while
  authenticated scopes can be applied to protected endpoints such as `/api/secure`.
* **Database migrations** – Flyway migrations version and evolve the MySQL
  schema, ensuring reproducible environments.

## Module layout

Song Quotes Service is built as a Maven multi-module project to keep the API
contract independent from the runtime implementation.

* **`api` module** – Owns the OpenAPI definition under
  `api/src/main/resources/openapi/song-quotes-service.yaml` and generates DTOs
  that can be shared with clients. The module also publishes the raw contract as
  a ZIP so consumers can download the exact specification that the application
  implements.
* **`application` module** – The Spring Boot service that depends on the
  generated API interfaces. During the build it unpacks the OpenAPI resources
  from the `api` module, generates controller interfaces with OpenAPI Generator,
  and provides the adapters, persistence, and security layers described in the
  architecture section above.

## Prerequisites

* Java 21
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
per environment.

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
Spotify artist metadata used by the enrichment adapter.

## REST API

All endpoints default to JSON and are served on port `8080`.

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/ping` | Liveness probe returning the serving pod/host name. |
| `GET` | `/api/quote/random` | Returns a random quote, or `404` when no quotes exist. |
| `GET` | `/api/quote/{id}` | Fetch a single quote by id. |
| `GET` | `/api/quotes` | Retrieve all quotes. |
| `GET` | `/api/quotes/count` | Count all quotes in storage. |
| `GET` | `/api/quotes/top10` | Return the ten most-viewed quotes. |
| `GET` | `/api/artist/{id}` | Fetch a single artist enriched with Spotify metadata. |
| `GET` | `/api/artists` | List artists with the number of quotes available for each. |
| `GET` | `/api/secure/ping` | Ping endpoint requiring a valid JWT, useful for smoke-testing auth flows. |
| `POST` | `/admin/quote` | Create a single quote and return the new id. |
| `POST` | `/admin/quotes` | Bulk-create quotes and return generated ids. |
| `PUT` | `/admin/quote/{id}` | Replace a quote (excluding restricted counters). |
| `PATCH` | `/admin/quote/{id}` | Partially update a quote (excluding restricted counters). |
| `DELETE` | `/admin/quote/{id}` | Delete a quote by id. |
| `GET` | `/admin/export` | Export all quotes as SQL insert statements for backup or migration purposes. |

## Observability

Micrometer metrics are emitted via Spring Boot Actuator. With the provided
configuration, Prometheus can scrape `/actuator/prometheus` and standard health
information is available under `/actuator/health` and `/actuator/info`.

## Testing & quality

Run the full verification suite (unit tests, integration tests, and coverage)
with Maven:

```bash
./mvnw clean verify
```

JaCoCo reports are written to `target/site/jacoco/index.html` and can be opened
in a browser for local inspection.

## Load testing

The `load-testing/random-quote-load-test.js` script defines a [k6](https://k6.io)
scenario that exercises the `GET /api/quote/random` endpoint exposed at
`http://192.168.1.139:30015` and validates basic latency and error-rate
thresholds that can be visualized in Grafana.

Run the script by pointing it at a deployed instance of the service:

```bash
ENDPOINT_URL=https://song-quotes.example.com/api/quote/random \
VUS=10 \
DURATION=5m \
SLEEP=0.5 \
k6 run load-testing/random-quote-load-test.js
```

You can adjust the virtual users (`VUS`), test `DURATION`, per-iteration delay
(`SLEEP`), or override the `ENDPOINT_URL` to target different environments. The
script exposes custom metrics (`random_quote_request_rate` and
`random_quote_request_duration`) that can be scraped by Prometheus and plotted
in Grafana dashboards alongside built-in k6 metrics.
