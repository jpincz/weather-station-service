package com.gamehouse.weather.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAggregationId implements Serializable {
    private String stationCode;
    private OffsetDateTime minuteWindow;
}
