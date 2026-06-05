# Changelog

---

## 2026-06-05

Upgraded to Java 21 and Spring Boot 3.3.6. Spring Cloud AWS bumped to 3.2.1. Base Docker image switched from `eclipse-temurin:17-jre-jammy` to `eclipse-temurin:21-jre-alpine`. JVM tuned for low-memory single-instance operation: SerialGC, 256 MB heap cap, 256k stack per thread. Virtual threads enabled. `flyway-database-postgresql` added — required for Flyway 10.x compatibility with Postgres 16 on Boot 3.3.x.

`OrderItem` entity refactored — bare `cupcakeId: Long` replaced with a proper `@ManyToOne Cupcake` reference. `V2__add_cupcake_fk.sql` formalises the FK constraint. `displayName` field added to `Cupcake` with `V3__add_cupcake_display_name.sql`. `OrderItemEmailDto` introduced for email rendering.

Both email templates rewritten — item list with cupcake images, per-item line totals, order reference block, and footer. `cupcake-orders-images` S3 bucket made public. `@Transactional` added to `DispatchEventSqsListener.receive` to fix `LazyInitializationException` on the `Order.items` collection when building dispatched email metadata. `.gitattributes` added to normalise line endings across WSL/Windows.

---
## 2026-06-04 | v1.3.0

Migrated from MySQL to Postgres. Flyway added for schema management — `ddl-auto` changed from `update` to `validate`, initial schema in `V1__init.sql`. Docker Compose healthcheck added for Postgres with `depends_on: condition: service_healthy` on the app container, replacing the Hikari indefinite retry workaround.

Deploy script extracted from inline SSM commands into `scripts/deploy.sh`, uploaded to S3 and executed as a single SSM command.

JPA entity fixes: bidirectional `Customer` ↔ `Order` relationship corrected with `mappedBy`, explicit `@JoinColumn` added to `OrderItem`, `CascadeType.ALL` on `Order → Customer` replaced with `PERSIST` and `MERGE`.

pom.xml: Spring Cloud AWS and Testcontainers versions moved into `dependencyManagement` BOMs, dead MapStruct properties removed, Flyway dependency added.

---

## 2026-06-01 | v1.2.0 

Nginx simplified to HTTP-only — CloudFront terminates SSL, Nginx just proxies to Spring Boot on port 80. HTTPS server block, HTTP→HTTPS redirect, and Let's Encrypt cert paths removed from `nginx.conf`. Port 443 and `/etc/letsencrypt` volume mount removed from `docker-compose.prod.yaml`.

Docker image now built for `linux/arm64` — `--platform linux/arm64` added to `docker build` in `deploy.yml` for Graviton (t4g) compatibility.

SSM deploy commands consolidated into a single shell script (`deploy.sh`) uploaded to S3 — fixes env var persistence across SSM command array steps.

---

## 2026-05-29

JaCoCo coverage reporting added (bumped from 0.8.7 to 0.8.12, report phase changed from `test` to `verify` to include integration tests). Codecov integration added via `codecov-action@v6` — initial coverage 74%. Build status, coverage, Java, and Spring Boot badges added to README.

---

## 2026-05-24

Spring Boot Actuator health endpoint (`/actuator/health`) added — used by the EC2 start Lambda to confirm the app is ready before updating DNS.

---

## 2026-05-21 | v1.1.0

Deploy pipeline updated to use SSM Session Manager instead of SSH. Files are now staged through S3 and pulled by the EC2 instance rather than copied via SCP. Instance ID is looked up dynamically by tag rather than stored as a static secret. SSH key and host secrets removed from GitHub.

---

## 2026-05-21 | v1.0.0

Initial release. Spring Boot cupcake ordering service with an event-driven architecture backed by AWS SQS. Orders are saved to MySQL, confirmation emails sent via SES, and a dispatch Lambda processes each order and triggers a follow-up email.

Kafka was evaluated and replaced with SQS — lower operational overhead, no broker to run, fits naturally on a single EC2 instance.

AWS SDK v1 replaced with v2. EC2 instance role used for all AWS auth — no credentials in config.

Nginx sits in front of the app as a reverse proxy, running as a Docker container in the same Compose network. HTTPS via Let's Encrypt on the EC2 host, with certs mounted read-only into the Nginx container.

Full test suite: unit tests with H2, integration tests with Testcontainers, SQS listener mocked in the test profile to avoid hitting AWS on every run.

GitHub Actions pipeline: build and test, push to ECR, deploy to EC2 via SSM.