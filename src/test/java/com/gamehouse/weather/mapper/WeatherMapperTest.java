package com.gamehouse.weather.mapper;

import com.gamehouse.weather.BaseTest;
import com.gamehouse.weather.dto.WeatherDto;
import com.gamehouse.weather.model.Weather;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WeatherMapperTest extends BaseTest {

    private final WeatherMapper mapper = Mappers.getMapper(WeatherMapper.class);

    @Test
    void shouldMapEntityToDto() {
        Weather entity = new Weather();
        entity.setId(1L);
        entity.setStationCode("ABC");
        entity.setCollectedAt(LocalDateTime.now().minusMinutes(5));
        entity.setReceivedAt(LocalDateTime.now());
        entity.setTemperature(25.5);
        entity.setHumidity(60.0);
        entity.setWindSpeed(12.3);

        WeatherDto dto = mapper.toDto(entity);

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
        WeatherDto dto = new WeatherDto(
                1L,
                "ABC",
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now(),
                25.5,
                60.0,
                12.3
        );

        Weather entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getStationCode()).isEqualTo(dto.getStationCode());
        assertThat(entity.getCollectedAt()).isEqualTo(dto.getCollectedAt());
        assertThat(entity.getReceivedAt()).isEqualTo(dto.getReceivedAt());
        assertThat(entity.getTemperature()).isEqualTo(dto.getTemperature());
        assertThat(entity.getHumidity()).isEqualTo(dto.getHumidity());
        assertThat(entity.getWindSpeed()).isEqualTo(dto.getWindSpeed());
    }
}
