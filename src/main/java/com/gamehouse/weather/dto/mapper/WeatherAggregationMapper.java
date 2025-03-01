package com.gamehouse.weather.dto.mapper;

import com.gamehouse.weather.dto.WeatherAggregationResponse;
import com.gamehouse.weather.model.WeatherAggregation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WeatherAggregationMapper {

    WeatherAggregationResponse toDto(WeatherAggregation entity);
}
