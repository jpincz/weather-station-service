package com.gamehouse.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class WeatherResponse {
    @Schema(description = "Unique identifier", example = "1")
    Long id;

    @Schema(description = "Three-letter station code", example = "ABC")
    String stationCode;

    @Schema(description = "Time at which weather data was collected", example = "2025-03-01T01:36:00")
    LocalDateTime collectedAt;

    @Schema(description = "Time at which weather data was received by this service", example = "2025-03-01T01:36:05")
    LocalDateTime receivedAt;

    @Schema(description = "Temperature value in Celsius", example = "23.5")
    Double temperature;

    @Schema(description = "Humidity percentage", example = "30.0")
    Double humidity;

    @Schema(description = "Wind speed in kilometers per hour", example = "12.0")
    Double windSpeed;
}
