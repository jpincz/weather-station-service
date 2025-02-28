package com.gamehouse.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class WeatherAggregationDto {

    private WeatherAggregationStatsDto temperature;
    private WeatherAggregationStatsDto humidity;
    private WeatherAggregationStatsDto windSpeed;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherAggregationStatsDto {
        private Double avg;
        private Double min;
        private Double max;
    }
}
