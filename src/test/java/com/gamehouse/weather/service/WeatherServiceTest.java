package com.gamehouse.weather.service;

import com.gamehouse.weather.BaseTest;
import com.gamehouse.weather.dto.WeatherDto;
import com.gamehouse.weather.mapper.WeatherMapper;
import com.gamehouse.weather.model.Weather;
import com.gamehouse.weather.repository.WeatherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest extends BaseTest {

    @Mock
    private WeatherRepository repository;

    @Mock
    private WeatherMapper mapper;

    @InjectMocks
    private WeatherService service;

    @Test
    void save_Valid_ShouldReturnDto() {
        WeatherDto validDto = new WeatherDto(
                null,
                "ABC",
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now(),
                25.0,
                50.0,
                10.0
        );

        Weather entity = new Weather(
                null,
                "ABC",
                validDto.getCollectedAt(),
                validDto.getReceivedAt(),
                validDto.getTemperature(),
                validDto.getHumidity(),
                validDto.getWindSpeed()
        );

        when(mapper.toEntity(any(WeatherDto.class))).thenReturn(entity);
        when(repository.save(any(Weather.class))).thenReturn(entity);
        when(mapper.toDto(any(Weather.class))).thenReturn(validDto);

        WeatherDto result = service.save(validDto);

        assertThat(result).isNotNull();
        assertThat(result.getStationCode()).isEqualTo(validDto.getStationCode());

        verify(repository, times(1)).save(entity);
    }

    @Test
    void save_InvalidCollectedAtField_ShouldThrowException() {
        WeatherDto dtoWithInvalidDates = new WeatherDto(
                null,
                "ABC",
                LocalDateTime.now().plusMinutes(10),
                LocalDateTime.now().minusMinutes(10),
                25.0,
                50.0,
                10.0
        );

        assertThatThrownBy(() -> service.save(dtoWithInvalidDates))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Collected time must not be in the future.");

        verify(repository, never()).save(any());
    }

    @Test
    void getLastByStation_Existing_ShouldReturnDto() {
        String stationCode = "ABC";
        LocalDateTime collectedAt = LocalDateTime.now().minusMinutes(15);
        LocalDateTime receivedAt = LocalDateTime.now();
        Weather weather = new Weather(
                1L,
                stationCode,
                collectedAt,
                receivedAt,
                20.0,
                55.0,
                8.0
        );
        WeatherDto expectedDto = new WeatherDto(
                1L,
                stationCode,
                collectedAt,
                receivedAt,
                20.0,
                55.0,
                8.0
        );

        when(repository.findFirstByStationCodeOrderByReceivedAtDesc(stationCode))
                .thenReturn(Optional.of(weather));
        when(mapper.toDto(weather)).thenReturn(expectedDto);

        WeatherDto result = service.getLastByStation(stationCode);

        assertThat(result).isNotNull();
        assertThat(result.getStationCode()).isEqualTo(stationCode);
        verify(repository, times(1)).findFirstByStationCodeOrderByReceivedAtDesc(stationCode);
        verify(mapper, times(1)).toDto(weather);
    }

    @Test
    void getLastByStation_NonExisting_ShouldThrowException() {
        String stationCode = "XYZ";
        when(repository.findFirstByStationCodeOrderByReceivedAtDesc(stationCode))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getLastByStation(stationCode))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No weather entry found for station: " + stationCode);
        verify(repository, times(1)).findFirstByStationCodeOrderByReceivedAtDesc(stationCode);
    }
}
