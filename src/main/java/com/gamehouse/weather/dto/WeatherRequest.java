package com.gamehouse.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherRequest {

    @NotNull(message = "Station code is required")
    @Size(min = 3, max = 3, message = "Station code must be exactly 3 characters")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Station code must be exactly 3 uppercase letters (e.g., ABC)")
    @Schema(description = "Three-letter station code", example = "ABC", required = true)
    private String stationCode;

    @NotNull(message = "Collected time is required")
    @Schema(description = "Time at which weather data was collected", example = "2025-03-01T01:36:00", required = true)
    private LocalDateTime collectedAt;

    @NotNull(message = "Temperature is required")
    @Schema(description = "Temperature value in Celsius", example = "23.5", required = true)
    private Double temperature;

    @NotNull(message = "Humidity is required")
    @DecimalMin(value = "0.0", message = "Humidity must be at least 0%")
    @DecimalMax(value = "100.0", message = "Humidity must be at most 100%")
    @Schema(description = "Humidity percentage", example = "30.0", required = true)
    private Double humidity;

    @NotNull(message = "Wind speed is required")
    @DecimalMin(value = "0.0", message = "Wind speed must be at least 0 km/h")
    @Schema(description = "Wind speed in kilometers per hour", example = "12.0", required = true)
    private Double windSpeed;
}
