package com.gamehouse.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
public class WeatherAggregationResponse {

    @Schema(description = "Temperature value in Celsius")
    WeatherAggregationStatsDto temperature;

    @Schema(description = "Humidity percentage")
    WeatherAggregationStatsDto humidity;

    @Schema(description = "Wind speed in kilometers per hour")
    WeatherAggregationStatsDto windSpeed;

    @Value
    public static class WeatherAggregationStatsDto {
        Double avg;
        Double min;
        Double max;
    }
}
