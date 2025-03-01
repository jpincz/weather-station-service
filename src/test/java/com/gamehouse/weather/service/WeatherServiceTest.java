package com.gamehouse.weather.service;

import com.gamehouse.weather.dto.WeatherRequest;
import com.gamehouse.weather.dto.WeatherResponse;
import com.gamehouse.weather.dto.mapper.WeatherMapper;
import com.gamehouse.weather.model.Weather;
import com.gamehouse.weather.repository.WeatherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WeatherRepository repository;

    @Mock
    private WeatherMapper mapper;

    @InjectMocks
    private WeatherService service;

    @Test
    void save_Valid_ShouldReturnDto() {
        WeatherRequest request = new WeatherRequest(
                "ABC",
                OffsetDateTime.now().minusMinutes(10),
                25.0,
                50.0,
                10.0
        );
        Weather unsavedEntity = new Weather(
                null,
                request.getStationCode(),
                request.getCollectedAt(),
                null,
                request.getTemperature(),
                request.getHumidity(),
                request.getWindSpeed()
        );
        Weather savedEntity = new Weather(
                1L,
                request.getStationCode(),
                request.getCollectedAt(),
                OffsetDateTime.now(),
                request.getTemperature(),
                request.getHumidity(),
                request.getWindSpeed()
        );
        WeatherResponse responseDto = new WeatherResponse(
                1L,
                request.getStationCode(),
                request.getCollectedAt(),
                savedEntity.getReceivedAt(),
                request.getTemperature(),
                request.getHumidity(),
                request.getWindSpeed()
        );

        when(mapper.toEntity(request)).thenReturn(unsavedEntity);
        when(repository.save(any(Weather.class))).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(responseDto);

        WeatherResponse result = service.save(request);

        assertThat(result).isNotNull();
        assertThat(result.getStationCode()).isEqualTo(request.getStationCode());
        assertThat(result.getCollectedAt()).isEqualTo(request.getCollectedAt());
        assertThat(result.getTemperature()).isEqualTo(request.getTemperature());
        assertThat(result.getHumidity()).isEqualTo(request.getHumidity());
        assertThat(result.getWindSpeed()).isEqualTo(request.getWindSpeed());
        assertThat(result.getId()).isNotNull();
        assertThat(result.getReceivedAt()).isNotNull();

        verify(mapper).toEntity(request);
        verify(repository).save(unsavedEntity);
        verify(mapper).toDto(savedEntity);
    }

    @Test
    void save_InvalidCollectedAtField_ShouldThrowException() {
        WeatherRequest request = new WeatherRequest(
                "ABC",
                OffsetDateTime.now().plusMinutes(10),
                25.0,
                50.0,
                10.0
        );
        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Collected time must not be in the future.");
        verify(repository, never()).save(any());
    }

    @Test
    void getLastByStation_Existing_ShouldReturnDto() {
        String stationCode = "ABC";
        OffsetDateTime collectedAt = OffsetDateTime.now().minusMinutes(15);
        OffsetDateTime receivedAt = OffsetDateTime.now();
        Weather weather = new Weather(
                1L,
                stationCode,
                collectedAt,
                receivedAt,
                20.0,
                55.0,
                8.0
        );
        WeatherResponse responseDto = new WeatherResponse(
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
        when(mapper.toDto(weather)).thenReturn(responseDto);

        WeatherResponse result = service.getLastByStation(stationCode);

        assertThat(result).isNotNull();
        assertThat(result.getStationCode()).isEqualTo(stationCode);
        assertThat(result.getCollectedAt()).isEqualTo(collectedAt);
        assertThat(result.getReceivedAt()).isEqualTo(receivedAt);

        verify(repository).findFirstByStationCodeOrderByReceivedAtDesc(stationCode);
        verify(mapper).toDto(weather);
    }

    @Test
    void getLastByStation_NonExisting_ShouldThrowException() {
        String stationCode = "XYZ";
        when(repository.findFirstByStationCodeOrderByReceivedAtDesc(stationCode))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getLastByStation(stationCode))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No weather entry found for station: " + stationCode);
        verify(repository).findFirstByStationCodeOrderByReceivedAtDesc(stationCode);
    }
}
