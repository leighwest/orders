# Orders Service

[![Build](https://github.com/leighwest/orders/actions/workflows/deploy.yml/badge.svg)](https://github.com/leighwest/orders/actions/workflows/deploy.yml)
[![codecov](https://codecov.io/gh/leighwest/orders/graph/badge.svg?token=24743b25-fbf6-4bed-8708-6cc4b39fc666)](https://codecov.io/gh/leighwest/orders)
[![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?logo=spring)](https://spring.io/projects/spring-boot)

A Spring Boot REST API for placing cupcake orders, built to demonstrate event-driven microservice architecture on AWS.

Customers place orders via a Swagger UI. The service persists the order, sends a confirmation email, and publishes an event to AWS SQS. A Lambda function acts as a dispatch microservice — it consumes the event, processes the order, and publishes a dispatch notification. The orders service consumes that notification and sends a final dispatch email to the customer.

The infrastructure is managed in a companion repo: [orders-infra](https://github.com/leighwest/orders-infra).

**Live API:** [https://cupcakes-api.leighwest.dev](https://cupcakes-api.leighwest.dev) _(requires the EC2 instance to be running — use the [instance starter](http://instance-starter.leighwest.dev) to boot it)_

---

## Architecture

```
Customer places order (Swagger UI)
            ↓
   Orders Service (Spring Boot / EC2)
   - Saves order to MySQL
   - Sends "order received" email via SES
   - Publishes to [SQS: order-created]
            ↓
   Dispatch Lambda (Node.js)
   - Consumes from [SQS: order-created]
   - Publishes to [SQS: order-dispatched]
            ↓
   Orders Service
   - Consumes from [SQS: order-dispatched]
   - Sends "order dispatched" email via SES
```

---

## Tech Stack

| Layer             | Technology                               |
| ----------------- | ---------------------------------------- |
| Language          | Java 17                                  |
| Framework         | Spring Boot 3.2.0                        |
| Messaging         | AWS SQS (Spring Cloud AWS 3.1.1)         |
| Storage           | AWS S3 (Spring Cloud AWS 3.1.1 / SDK v2) |
| Database          | MySQL (prod), H2 (unit tests)            |
| Email             | AWS SES + Thymeleaf templates            |
| Testing           | JUnit 5, Mockito, Testcontainers         |
| Infrastructure    | Terraform (see orders-infra)             |
| CI/CD             | GitHub Actions                           |
| Container runtime | Docker, Docker Compose                   |

---

## CI/CD

Every push to `main` triggers the deploy pipeline:

```
Push to main
     ↓
Build & test (Maven)
     ↓
Build Docker image → push to ECR
     ↓
Start EC2 instance
     ↓
Upload deploy files to S3
     ↓
SSM send-command → pull image → docker-compose up
```

The pipeline is defined in `.github/workflows/deploy.yml`.

### Secrets required

| Secret                  | Description                                         |
| ----------------------- | --------------------------------------------------- |
| `AWS_ACCESS_KEY_ID`     | IAM credentials for ECR push, EC2 start, SSM access |
| `AWS_SECRET_ACCESS_KEY` | IAM credentials                                     |

Application secrets (`MYSQL_ROOT_PASSWORD`, `MAIL_USERNAME`, `MAIL_PASSWORD`) are fetched at deploy time from AWS Systems Manager Parameter Store — not stored in GitHub secrets. Instance ID is looked up dynamically by tag at deploy time.

---

## Local Development

### Prerequisites

- Java 17
- Docker Desktop
- AWS CLI configured with credentials for `ap-southeast-4`
- AWS SQS queues provisioned (see [orders-infra](https://github.com/leighwest/orders-infra))

### Setup

1. Clone the repo:

```bash
git clone https://github.com/leighwest/orders.git
cd orders
```

2. Create a `.env` file in the project root:

```
MAIL_USERNAME=your-gmail@example.com
MAIL_PASSWORD=your-gmail-app-password
```

3. Start MySQL:

```bash
docker-compose up -d
```

4. Run the application with the local profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

5. Open Swagger UI: [http://localhost:8080](http://localhost:8080)

The `local` profile connects to the Docker MySQL instance and uses your AWS CLI credentials (`~/.aws/credentials`) for SQS and S3 access. No AWS credentials should appear in any config file.

---

## End-to-End Verification

Once running locally, verify the full event-driven flow:

1. Place a test order via Swagger UI
2. Confirm the order is saved (check the DB or the `GET /orders` endpoint)
3. Check your inbox — "order received" email should arrive
4. In the AWS console, navigate to SQS → `order-created` — the message should have been consumed by the Lambda
5. Navigate to Lambda → `orders-dispatch` → Monitor → CloudWatch Logs — confirm the Lambda executed
6. Check your inbox again — "order dispatched" email should arrive

---

## Running Tests

```bash
./mvnw test
```

Integration tests use Testcontainers and require Docker Desktop to be running. Unit and service tests use H2 in-memory and have no external dependencies.

| Test type      | Database               | SQS                  |
| -------------- | ---------------------- | -------------------- |
| Unit / service | H2 in-memory           | Mocked               |
| Integration    | Testcontainers (MySQL) | Mocked (`@MockBean`) |

---

## Key Design Decisions

| Decision                             | Rationale                                                                                   |
| ------------------------------------ | ------------------------------------------------------------------------------------------- |
| SQS over Kafka                       | Managed service, no broker to operate, fits single EC2 deployment                           |
| Real AWS SQS locally                 | Consistent with prod, stronger demo story than LocalStack                                   |
| EC2 instance role                    | No credentials in config — AWS best practice                                                |
| Lambda as dispatch service           | Realistic microservices pattern without a second full service                               |
| Secrets in Parameter Store           | Single source of truth, no duplication across GitHub secrets and config files               |
| Docker Compose in prod               | Appropriate for single-instance hobby project — ECS/EKS would be the enterprise equivalent  |
| Testcontainers for integration tests | Real MySQL dialect, isolated per run                                                        |
| H2 for unit tests                    | Fast, no Docker required                                                                    |
| SSM Session Manager over SSH         | No open ports, IAM-controlled access, full audit trail — enterprise standard for EC2 access |
| S3 staging for deploy artefacts      | Replaces SCP — runner uploads, EC2 pulls via instance role. No SSH needed                   |
| Dynamic instance ID lookup           | Looked up by tag at deploy time — no static secret to update when instance is recreated     |

---

## Related

- [orders-infra](https://github.com/leighwest/orders-infra) — Terraform infrastructure for this service

---

## Versions

| Version                                                    | Description                                                                   |
| ---------------------------------------------------------- | ----------------------------------------------------------------------------- |
| [v1.0.0](https://github.com/leighwest/orders/tree/v1.0.0)  | Spring Boot 17, MySQL, GitHub Actions CI/CD via SSH, HTTPS via Let's Encrypt  |
| [v1.1.0](https://github.com/leighwest/orders/tree/v1.1.0)  | SSM Session Manager replaces SSH, S3 file staging, dynamic instance ID lookup |
| [v2.0.0](https://github.com/leighwest/orders/tree/v2.0.0)  | TBD                                                                           |
