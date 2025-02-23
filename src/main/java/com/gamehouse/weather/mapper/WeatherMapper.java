package com.gamehouse.weather.mapper;

import com.gamehouse.weather.dto.WeatherDto;
import com.gamehouse.weather.model.Weather;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WeatherMapper {
    WeatherDto toDto(Weather entity);

    Weather toEntity(WeatherDto dto);
}
