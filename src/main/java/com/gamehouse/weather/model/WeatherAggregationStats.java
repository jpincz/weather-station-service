package com.gamehouse.weather.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAggregationStats {

    @Column(nullable = false)
    private Double avg;

    @Column(nullable = false)
    private Double min;

    @Column(nullable = false)
    private Double max;
}
