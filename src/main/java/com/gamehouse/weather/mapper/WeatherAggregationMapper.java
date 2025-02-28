package com.gamehouse.weather.mapper;

import com.gamehouse.weather.dto.WeatherAggregationDto;
import com.gamehouse.weather.model.WeatherAggregation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WeatherAggregationMapper {

    WeatherAggregationDto toDto(WeatherAggregation entity);

    WeatherAggregation toEntity(WeatherAggregationDto dto);
}
