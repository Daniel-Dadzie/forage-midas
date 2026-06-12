# Midas Core

A Spring Boot application developed as part of the JPMC Advanced Software Engineering Forage program. Midas Core is a financial transaction processing system that integrates Kafka messaging, relational database persistence, and REST APIs for balance querying and transaction management.

## Project Overview

Midas Core serves as a microservice for processing and managing financial transactions. It listens to trading updates from Kafka, persists transaction data to a database, and provides REST endpoints to query account balances. The application leverages Spring Boot's robust ecosystem along with Apache Kafka for event-driven architecture.

## Prerequisites

- **Java**: JDK 17 or higher
- **Maven**: 3.6.0 or higher
- **Kafka**: Running Kafka broker (for production use)
- **H2 Database**: Embedded database (included via Maven dependency)

## Getting Started

### Build the Application

```bash
mvn clean install
```

Or using Maven wrapper on Windows:

```bash
mvnw.cmd clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

Or using Maven wrapper:

```bash
mvnw.cmd spring-boot:run
```

The application will start on <http://localhost:33400>

### Run Tests

Execute all test suites:

```bash
mvn test
```

Run a specific test class:

```bash
mvn test -Dtest=TaskOneTests
```

## Project Structure

```text
forage-midas/
├── src/
│   ├── main/
│   │   └── java/com/jpmc/midascore/
│   │       ├── MidasCoreApplication.java       # Main Spring Boot application entry point
│   │       ├── component/                      # Core business components
│   │       │   ├── BalanceController.java      # REST endpoints for balance queries
│   │       │   ├── DatabaseConduit.java        # Database access layer
│   │       │   └── TransactionListener.java    # Kafka consumer for transaction events
│   │       ├── entity/                         # JPA entity models
│   │       ├── repository/                     # Spring Data JPA repositories
│   │       └── foundation/                     # Foundation/utility classes
│   └── test/
│       └── java/com/jpmc/midascore/
│           ├── TaskOneTests.java               # Task 1 test suite
│           ├── TaskTwoTests.java               # Task 2 test suite
│           ├── TaskThreeTests.java             # Task 3 test suite
│           ├── TaskFourTests.java              # Task 4 test suite
│           ├── TaskFiveTests.java              # Task 5 test suite
│           ├── BalanceQuerier.java             # Utility for balance queries
│           ├── FileLoader.java                 # Test data file loader
│           ├── KafkaProducer.java              # Kafka producer for testing
│           └── UserPopulator.java              # Test user data population
├── pom.xml                                     # Maven build configuration
├── application.yml                             # Spring Boot configuration
└── README.md                                   # This file
```

## Configuration

Key settings in `application.yml`:

- **Server Port**: 33400
- **Kafka Topic**: `trader-updates`
- **Database**: H2 (in-memory)
- **Kafka Deserializer**: JSON with trusted package `*`

## Architecture

### Components

- **BalanceController**: REST API endpoint layer for querying account balances
- **TransactionListener**: Kafka consumer that processes incoming transaction events from the `trader-updates` topic
- **DatabaseConduit**: Data access object handling persistence operations to the H2 database
- **Entity Models**: JPA entities representing core domain objects (transactions, balances, etc.)

### Data Flow

1. **Event Ingestion**: Transaction events arrive via Kafka topic `trader-updates`
2. **Processing**: `TransactionListener` consumes and processes events
3. **Persistence**: Data is stored via `DatabaseConduit` to the H2 database
4. **Query**: REST endpoints expose balance and transaction information

### Technology Stack

- **Framework**: Spring Boot 3.2.5
- **Language**: Java 17
- **ORM**: Spring Data JPA (Hibernate)
- **Database**: H2 (embedded)
- **Messaging**: Apache Kafka with Spring Kafka
- **Build**: Maven

## Test Suites

The project includes five comprehensive test task suites:

- **TaskOneTests**: Foundation tests for core functionality
- **TaskTwoTests**: Intermediate functionality tests
- **TaskThreeTests**: Advanced feature tests
- **TaskFourTests**: Integration tests
- **TaskFiveTests**: End-to-end scenario tests

Test data is located in `src/test/resources/test_data/`

## Usage Examples

### Query Account Balance

```bash
curl http://localhost:33400/balance/{accountId}
```

### Health Check

The application includes Spring Boot Actuator endpoints for monitoring:

```bash
curl http://localhost:33400/actuator/health
```

## Building and Deployment

### Build JAR

```bash
mvn clean package
```

This creates an executable JAR in `target/midas-core-1.0.0.jar`

### Run JAR

```bash
java -jar target/midas-core-1.0.0.jar
```

## Development Notes

- The application uses H2 embedded database by default for simplicity
- Kafka integration allows for scalable event-driven architecture
- Spring Boot Actuator is included for production-ready monitoring and metrics
- JPA repositories provide type-safe database access patterns

## Troubleshooting

- **Kafka Connection Issues**: Ensure a Kafka broker is running on the configured address
- **Port Already in Use**: Change the port in `application.yml` if port 33400 is unavailable
- **Test Failures**: Verify test data files exist in `src/test/resources/test_data/`
