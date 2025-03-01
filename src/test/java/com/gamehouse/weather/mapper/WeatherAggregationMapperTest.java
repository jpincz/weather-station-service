package com.gamehouse.weather.mapper;

import com.gamehouse.weather.dto.WeatherAggregationResponse;
import com.gamehouse.weather.dto.mapper.WeatherAggregationMapper;
import com.gamehouse.weather.model.WeatherAggregation;
import com.gamehouse.weather.model.WeatherAggregationStats;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WeatherAggregationMapperTest {

    private final WeatherAggregationMapper mapper = Mappers.getMapper(WeatherAggregationMapper.class);

    @Test
    void toDto_shouldMapEntityToDto() {
        OffsetDateTime time = OffsetDateTime.now().withSecond(0).withNano(0);
        WeatherAggregationStats temperature = new WeatherAggregationStats(21.0, 20.0, 22.0);
        WeatherAggregationStats humidity = new WeatherAggregationStats(52.5, 50.0, 55.0);
        WeatherAggregationStats windSpeed = new WeatherAggregationStats(6.0, 5.0, 7.0);
        WeatherAggregation entity = new WeatherAggregation("ABC", time, 100L, temperature, humidity, windSpeed);
        WeatherAggregationResponse dto = mapper.toDto(entity);
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
}
