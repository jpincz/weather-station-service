package com.gamehouse.weather.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WeatherDto {
    private Long id;

    @NotNull(message = "Station code is required")
    @Size(min = 3, max = 3, message = "Station code must be exactly 3 characters")
    private String stationCode;

    @NotNull(message = "Collected time is required")
    private LocalDateTime collectedAt;

    private LocalDateTime receivedAt;

    @NotNull(message = "Temperature is required")
    private Double temperature;

    @NotNull(message = "Humidity is required")
    @DecimalMin(value = "0.0", message = "Humidity must be at least 0%")
    @DecimalMax(value = "100.0", message = "Humidity must be at most 100%")
    private Double humidity;

    @NotNull(message = "Wind speed is required")
    @DecimalMin(value = "0.0", message = "Wind speed must be at least 0 km/h")
    private Double windSpeed;
}
