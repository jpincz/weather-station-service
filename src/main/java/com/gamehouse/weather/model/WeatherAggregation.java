package com.gamehouse.weather.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@IdClass(WeatherAggregationId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAggregation {

    @Id
    @Column(nullable = false)
    private String stationCode;

    @Id
    @Column(nullable = false)
    private OffsetDateTime minuteWindow;

    @Column(nullable = false)
    private Long totalRecords;

    @Embedded
    @AttributeOverride(name = "avg", column = @Column(name = "avg_temperature", nullable = false))
    @AttributeOverride(name = "min", column = @Column(name = "min_temperature", nullable = false))
    @AttributeOverride(name = "max", column = @Column(name = "max_temperature", nullable = false))
    private WeatherAggregationStats temperature;

    @Embedded
    @AttributeOverride(name = "avg", column = @Column(name = "avg_humidity", nullable = false))
    @AttributeOverride(name = "min", column = @Column(name = "min_humidity", nullable = false))
    @AttributeOverride(name = "max", column = @Column(name = "max_humidity", nullable = false))
    private WeatherAggregationStats humidity;

    @Embedded
    @AttributeOverride(name = "avg", column = @Column(name = "avg_wind_speed", nullable = false))
    @AttributeOverride(name = "min", column = @Column(name = "min_wind_speed", nullable = false))
    @AttributeOverride(name = "max", column = @Column(name = "max_wind_speed", nullable = false))
    private WeatherAggregationStats windSpeed;
}

