package com.gamehouse.weather.dto.mapper;

import com.gamehouse.weather.dto.WeatherRequest;
import com.gamehouse.weather.dto.WeatherResponse;
import com.gamehouse.weather.model.Weather;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WeatherMapper {

    WeatherResponse toDto(Weather entity);

    Weather toEntity(WeatherRequest request);
}
