# Changelog

---

## 2026-06-01

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