package com.gamehouse.weather.mapper;

import com.gamehouse.weather.dto.WeatherAggregationDto;
import com.gamehouse.weather.dto.WeatherAggregationDto.WeatherAggregationStatsDto;
import com.gamehouse.weather.model.WeatherAggregation;
import com.gamehouse.weather.model.WeatherAggregationStats;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WeatherAggregationMapperTest {

    private final WeatherAggregationMapper mapper = Mappers.getMapper(WeatherAggregationMapper.class);

    @Test
    void toDto_shouldMapEntityToDto() {
        LocalDateTime time = LocalDateTime.now().withSecond(0).withNano(0);
        WeatherAggregationStats temperature = new WeatherAggregationStats(21.0, 20.0, 22.0);
        WeatherAggregationStats humidity = new WeatherAggregationStats(52.5, 50.0, 55.0);
        WeatherAggregationStats windSpeed = new WeatherAggregationStats(6.0, 5.0, 7.0);
        WeatherAggregation entity = new WeatherAggregation("ABC", time, 100L, temperature, humidity, windSpeed);
        WeatherAggregationDto dto = mapper.toDto(entity);
        assertThat(dto.getTemperature().getAvg()).isEqualTo(temperature.getAvg());
        assertThat(dto.getTemperature().getMin()).isEqualTo(temperature.getMin());
        assertThat(dto.getTemperature().getMax()).isEqualTo(temperature.getMax());
        assertThat(dto.getHumidity().getAvg()).isEqualTo(humidity.getAvg());
        assertThat(dto.getHumidity().getMin()).isEqualTo(humidity.getMin());
        assertThat(dto.getHumidity().getMax()).isEqualTo(humidity.getMax());
        assertThat(dto.getWindSpeed().getAvg()).isEqualTo(windSpeed.getAvg());
        assertThat(dto.getWindSpeed().getMin()).isEqualTo(windSpeed.getMin());
        assertThat(dto.getWindSpeed().getMax()).isEqualTo(windSpeed.getMax());
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        LocalDateTime time = LocalDateTime.now().withSecond(0).withNano(0);
        WeatherAggregationStatsDto temperatureDto = new WeatherAggregationStatsDto(21.0, 20.0, 22.0);
        WeatherAggregationStatsDto humidityDto = new WeatherAggregationStatsDto(52.5, 50.0, 55.0);
        WeatherAggregationStatsDto windSpeedDto = new WeatherAggregationStatsDto(6.0, 5.0, 7.0);
        WeatherAggregationDto dto = new WeatherAggregationDto(temperatureDto, humidityDto, windSpeedDto);
        WeatherAggregation entity = mapper.toEntity(dto);
        assertThat(entity.getTemperature().getAvg()).isEqualTo(temperatureDto.getAvg());
        assertThat(entity.getTemperature().getMin()).isEqualTo(temperatureDto.getMin());
        assertThat(entity.getTemperature().getMax()).isEqualTo(temperatureDto.getMax());
        assertThat(entity.getHumidity().getAvg()).isEqualTo(humidityDto.getAvg());
        assertThat(entity.getHumidity().getMin()).isEqualTo(humidityDto.getMin());
        assertThat(entity.getHumidity().getMax()).isEqualTo(humidityDto.getMax());
        assertThat(entity.getWindSpeed().getAvg()).isEqualTo(windSpeedDto.getAvg());
        assertThat(entity.getWindSpeed().getMin()).isEqualTo(windSpeedDto.getMin());
        assertThat(entity.getWindSpeed().getMax()).isEqualTo(windSpeedDto.getMax());
    }
}
