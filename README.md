# Orders Service

A Spring Boot REST API for placing cupcake orders, built to demonstrate event-driven microservice architecture on AWS.

Customers place orders via a Swagger UI. The service persists the order, sends a confirmation email, and publishes an event to AWS SQS. A Lambda function acts as a dispatch microservice — it consumes the event, processes the order, and publishes a dispatch notification. The orders service consumes that notification and sends a final dispatch email to the customer.

The infrastructure is managed in a companion repo: [orders-infra](https://github.com/leighwest/orders-infra).

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

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.0 |
| Messaging | AWS SQS (Spring Cloud AWS 3.1.1) |
| Storage | AWS S3 (Spring Cloud AWS 3.1.1 / SDK v2) |
| Database | MySQL (prod), H2 (unit tests) |
| Email | AWS SES + Thymeleaf templates |
| Testing | JUnit 5, Mockito, Testcontainers |
| Infrastructure | Terraform (see orders-infra) |
| Local dev | Docker Compose |

---

## Local Development

### Prerequisites

- Java 17
- Docker Desktop
- Maven (or use the included `./mvnw` wrapper)
- AWS CLI configured with credentials for `ap-southeast-4`
- AWS SQS queues provisioned (see [orders-infra](https://github.com/leighwest/orders-infra))

### Setup

1. Clone the repo:
```bash
git clone https://github.com/leighwest/orders.git
cd orders
```

2. Create a `.env` file in the project root for email credentials:
```
MAIL_USERNAME=your-email@example.com
MAIL_PASSWORD=your-smtp-password
```

3. Start MySQL:
```bash
docker-compose up -d
```

4. Run the application with the local profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

5. Open Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

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

| Test type | Database | SQS |
|---|---|---|
| Unit / service | H2 in-memory | Mocked |
| Integration | Testcontainers (MySQL) | Mocked (`@MockBean`) |

---

## Key Design Decisions

| Decision | Rationale |
|---|---|
| SQS over Kafka | Managed service, no broker to operate, fits single EC2 deployment |
| Real AWS SQS locally | Consistent with prod, stronger demo story than LocalStack |
| EC2 instance role | No credentials in config — AWS best practice |
| Lambda as dispatch service | Realistic microservices pattern without a second full service |
| Testcontainers for integration tests | Real MySQL dialect, isolated per run |
| H2 for unit tests | Fast, no Docker required |

---

## Related

- [orders-infra](https://github.com/leighwest/orders-infra) — Terraform infrastructure for this service