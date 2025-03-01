package com.gamehouse.weather.mapper;

import com.gamehouse.weather.dto.WeatherRequest;
import com.gamehouse.weather.dto.WeatherResponse;
import com.gamehouse.weather.dto.mapper.WeatherMapper;
import com.gamehouse.weather.model.Weather;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WeatherMapperTest {

    private final WeatherMapper mapper = Mappers.getMapper(WeatherMapper.class);

    @Test
    void shouldMapEntityToDto() {
        Weather entity = new Weather();
        entity.setId(1L);
        entity.setStationCode("ABC");
        entity.setCollectedAt(OffsetDateTime.now().minusMinutes(5));
        entity.setReceivedAt(OffsetDateTime.now());
        entity.setTemperature(25.5);
        entity.setHumidity(60.0);
        entity.setWindSpeed(12.3);

        WeatherResponse dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getStationCode()).isEqualTo(entity.getStationCode());
        assertThat(dto.getCollectedAt()).isEqualTo(entity.getCollectedAt());
        assertThat(dto.getReceivedAt()).isEqualTo(entity.getReceivedAt());
        assertThat(dto.getTemperature()).isEqualTo(entity.getTemperature());
        assertThat(dto.getHumidity()).isEqualTo(entity.getHumidity());
        assertThat(dto.getWindSpeed()).isEqualTo(entity.getWindSpeed());
    }

    @Test
    void shouldMapDtoToEntity() {
        WeatherRequest dto = new WeatherRequest(
                "ABC",
                OffsetDateTime.now().minusMinutes(5),
                25.5,
                60.0,
                12.3
        );

        Weather entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getStationCode()).isEqualTo(dto.getStationCode());
        assertThat(entity.getCollectedAt()).isEqualTo(dto.getCollectedAt());
        assertThat(entity.getTemperature()).isEqualTo(dto.getTemperature());
        assertThat(entity.getHumidity()).isEqualTo(dto.getHumidity());
        assertThat(entity.getWindSpeed()).isEqualTo(dto.getWindSpeed());
    }
}
