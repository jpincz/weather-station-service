package com.gamehouse.weather.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Station code is required")
    @Size(min = 3, max = 3, message = "Station code must be exactly 3 characters")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Station code must be exactly 3 uppercase letters (e.g., ABC)")
    @Column(nullable = false, length = 3)
    private String stationCode;

    @NotNull(message = "Collected time is required")
    @Column(nullable = false)
    private OffsetDateTime collectedAt;

    @Column(nullable = false)
    private OffsetDateTime receivedAt;

    @NotNull(message = "Temperature is required")
    @Column(nullable = false)
    private Double temperature;

    @NotNull(message = "Humidity is required")
    @DecimalMin(value = "0.0", message = "Humidity must be at least 0%")
    @DecimalMax(value = "100.0", message = "Humidity must be at most 100%")
    @Column(nullable = false)
    private Double humidity;

    @NotNull(message = "Wind speed is required")
    @DecimalMin(value = "0.0", message = "Wind speed must be at least 0 km/h")
    @Column(nullable = false)
    private Double windSpeed;
}
