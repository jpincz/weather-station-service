# Weather Station Backend Test

This project implements a backend service for managing weather data from multiple weather stations. It was developed as
a technical test for a Backend Engineer role at GameHouse, demonstrating best practices in data handling, service
orchestration, and alert functionality.


## Overview

The service receives weather data from stations worldwide, processes the information, and provides endpoints for:

- Saving weather data
- Retrieving the last received weather entry for a given station
- Aggregating weather data (future enhancements may include average, min, and max calculations)

In addition, the application includes alert functionality for:

- Missing data alerts (for stations that haven’t reported within a specified window)
- Temperature threshold alerts (triggered when the average temperature on a station over a given time period exceeds a set threshold)


## Features

- **REST API Endpoints:**
  - `POST /weather`: Save weather data.
  - `GET /weather/{stationCode}/last`: Retrieve the most recent weather data for a specific station.
  - `GET /weather/{stationCode}/range?start={startDate}&end={endDate}`: Retrieves aggregated weather data (average,
    minimum, and maximum values) for the specified station over the given date range.

- **API Documentation:**
  - The service exposes a Swagger UI for interactive API documentation. Once the application is running, access it at:  
    [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui.html)


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


## Additional Notes

- **Swagger UI:**  
  Use the Swagger UI to explore and test the API endpoints interactively
  at [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui.html).

- **Alerts:**  
  Alerts are logged to the standard output with timestamps for easy monitoring.
