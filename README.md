# Weather Station Backend Test

This project implements a backend service for managing weather data from multiple weather stations. It was developed as
a technical test for a Backend Engineer role at GameHouse, demonstrating best practices in data handling, service
orchestration, and alert functionality.

## Overview

The service receives weather data from stations worldwide, processes the information, and provides endpoints for:

- Saving weather data.
- Retrieving the last received weather entry for a given station.
- Aggregating weather data (future enhancements may include average, minimum, and maximum calculations).

In addition, the application includes alert functionality for:

- Missing data alerts (for stations that haven’t reported within a specified window).
- Temperature threshold alerts (triggered when a station’s average temperature over a given period exceeds a set
  threshold).

## Features

- **REST API Endpoints:**
    - `POST /weather`: Save weather data.
    - `GET /weather/{stationCode}/last`: Retrieve the most recent weather data for a specific station.
    - `GET /weather/{stationCode}/range?start={startDate}&end={endDate}`: Retrieve aggregated weather data (average,
      minimum, and maximum values) for the specified station over a given date range.

- **API Documentation:**
    - Interactive API documentation is available via Swagger UI at:  
      [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui.html)

- **Alerts:**
    - Alerts are logged to the standard output with timestamps for easy monitoring.

## Run Locally

### Prerequisites

- **Java 17**
- **Maven 3.8+**
- **Docker**

### Steps

1. **Start Docker:**  
   Ensure Docker is running on your machine.

2. **Clone the repository and navigate to the project folder:**
   ```sh
   git clone git@github.com:jpincz/weather-station-service.git
   cd weather-station-service
   ```

3. **Build the project with Maven:**
   ```sh
   mvn clean package
   ```

4. **Start database container:**  
   Use Docker Compose to start only the database in detached mode:
   ```sh
   docker-compose up -d db
   ```

5. **Run application (Choose One Option):**

- **Run with Maven (with ALERT_TEMPERATURE_THRESHOLD set as needed)**
  ```sh
  ALERT_TEMPERATURE_THRESHOLD=45 mvn spring-boot:run
  ```
- **Run with Docker Compose (ALERT_TEMPERATURE_THRESHOLD is set on docker-compose.yml file)**
   ```sh
   docker-compose up app
   ```

## Development Process and Design Considerations

### Aggregation Strategy and Trade-offs

The main challenge was implementing per-minute data aggregations within the project constraints. I considered these
options:

- **Materialized View**
- **On-the-fly Calculation on Each Create Request**
- **Asynchronous Processing with a Cron Job**
- **Stored Procedure**

**Decision: Stored Procedure**

- **Pros:**
    - Offloads computation to the database, reducing memory usage in the application.
    - Ensures high accuracy and consistency.
    - More performant than ad-hoc queries.
- **Cons:**
    - Couples write operations with aggregations, adding overhead.
    - Harder to maintain, less experienced developers might find them challenging to understand and modify.
    - Potential portability issues if migrating to a different database vendor in the future.
    - Can be hard to test and debug, and have limited tooling (compared to normal IDEs for application code)

For larger systems, a decoupled approach is typically more effective. Some strategies include:

- **Scheduled Cron Jobs:**  
  Run aggregation tasks at fixed intervals to avoid interfering with real-time data insertion, possibly using a read
  replica to reduce the load.
- **Dedicated Reporting Database:**  
  Use a separate database optimized for analytical queries to handle heavy query loads without affecting transactional
  performance (e.g., using Snowflake updated by ETL processes from the primary database).
- **CQRS Architecture:**  
  Separate write (command) and read (query) responsibilities to allow independent tuning and scaling. For example, using
  PostgreSQL for writes while synchronizing with a read database like Elasticsearch for complex aggregation queries.

### Quality Assurance

- **Test-Driven Development (TDD):**  
  Every change was supported by tests to verify correct behavior
- **Integration Testing:**  
  Utilized Testcontainers to simulate "production" environments.

### Performance Considerations

- **Memory Optimization:**
    - JVM settings: `-Xms64m`, `-Xmx256m`, `-XX:+UseSerialGC`, `-Xss256k`
    - Spring Boot properties: `spring.jmx.enabled=false`, `spring.main.lazy-initialization=true`
    - Future improvements could include fine-tuning Tomcat settings (e.g., spare threads, max threads, timeouts) and
      disabling unused auto-configured beans.
- **Database Performance:**
    - All queries are optimized with appropriate indexes.

### Additional Notes

- **API Documentation:**  
  The Swagger UI is available at [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui.html).
- The property names, endpoint structure, and exposed endpoints strictly follow the provided examples.

