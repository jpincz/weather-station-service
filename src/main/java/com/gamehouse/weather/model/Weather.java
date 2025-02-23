package com.gamehouse.weather.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Station code is required")
    @Size(min = 3, max = 3, message = "Station code must be exactly 3 characters")
    @Column(nullable = false, length = 3)
    private String stationCode;

    @NotNull(message = "Collected time is required")
    @Column(nullable = false)
    private LocalDateTime collectedAt;

    @NotNull(message = "Received time is required")
    @Column(nullable = false)
    private LocalDateTime receivedAt;

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
