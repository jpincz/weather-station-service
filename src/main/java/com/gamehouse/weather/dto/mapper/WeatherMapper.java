package com.gamehouse.weather.dto.mapper;

import com.gamehouse.weather.dto.WeatherRequest;
import com.gamehouse.weather.dto.WeatherResponse;
import com.gamehouse.weather.model.Weather;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {java.time.ZoneOffset.class})
public interface WeatherMapper {

    @Mapping(target = "collectedAt", expression = "java(request.getCollectedAt().withOffsetSameInstant(ZoneOffset.UTC))")
    Weather toEntity(WeatherRequest request);

    @Mapping(target = "collectedAt", expression = "java(entity.getCollectedAt().withOffsetSameInstant(ZoneOffset.UTC))")
    WeatherResponse toDto(Weather entity);
}
