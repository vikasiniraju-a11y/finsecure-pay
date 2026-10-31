# FinSecure Pay – DevOps & Cloud AI Capstone

## Project Overview

FinSecure Pay is a Java Spring Boot REST API developed as part of a DevOps and Cloud AI capstone project.

The application validates financial transactions using predefined business rules and demonstrates application development, automated testing, and version control.

## Technology Stack

- Java 21
- Spring Boot 4.1.2
- Maven
- JUnit
- Git and GitHub

## API Endpoints

### GET /health

Checks application health.

Expected response:

`{"status":"UP"}`

### POST /validate

Validates a transaction using transaction ID, account number, transaction amount, and currency.

Example request:

```json
{
  "transactionId": "TXN1001",
  "accountNumber": "ACC1001",
  "amount": 500,
  "currency": "SGD"
}
```

Expected response:

```json
{
  "transactionId": "TXN1001",
  "status": "APPROVED",
  "message": "Transaction validated successfully"
}
```

## Validation Rules

- Transaction ID must not be empty.
- Account number must not be empty.
- Transaction amount must be greater than zero.
- Supported currencies: SGD, USD, EUR.

## Unit Testing

The application includes eight transaction validation tests and one Spring Boot application context test.

**JUnit Results:**
- Total tests: 9
- Passed: 9
- Failed: 0
- Errors: 0

## Build Instructions

Run from the project root on Windows:

`.\mvnw.cmd clean package`

## Run Instructions

`.\mvnw.cmd spring-boot:run`

The application runs on port 8080.

## Project Status

Task 1 – Application Development and Version Control completed.
