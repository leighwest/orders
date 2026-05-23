# Changelog

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