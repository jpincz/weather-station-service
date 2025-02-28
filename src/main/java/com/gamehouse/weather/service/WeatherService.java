package com.gamehouse.weather.service;

import com.gamehouse.weather.dto.WeatherAggregationDto;
import com.gamehouse.weather.dto.WeatherDto;
import com.gamehouse.weather.mapper.WeatherAggregationMapper;
import com.gamehouse.weather.mapper.WeatherMapper;
import com.gamehouse.weather.model.Weather;
import com.gamehouse.weather.model.WeatherAggregation;
import com.gamehouse.weather.repository.WeatherAggregationRepository;
import com.gamehouse.weather.repository.WeatherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherRepository repository;
    private final WeatherMapper mapper;
    private final WeatherAggregationRepository weatherAggregationRepository;
    private final WeatherAggregationMapper weatherAggregationMapper;

    @Transactional
    public WeatherDto save(WeatherDto dto) {
        LocalDateTime currentDate = LocalDateTime.now();
        if (dto.getCollectedAt().isAfter(currentDate)) {
            throw new IllegalArgumentException("Collected time must not be in the future.");
        }

        dto.setReceivedAt(currentDate);
        Weather entity = mapper.toEntity(dto);
        Weather savedEntity = repository.save(entity);
        return mapper.toDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public WeatherDto getLastByStation(String stationCode) {
        Weather weather = repository.findFirstByStationCodeOrderByReceivedAtDesc(stationCode)
                .orElseThrow(() -> new EntityNotFoundException("No weather entry found for station: " + stationCode));
        return mapper.toDto(weather);
    }

    @Transactional(readOnly = true)
    public WeatherAggregationDto getAggregationByStationAndDateRange(String stationCode, LocalDateTime start, LocalDateTime end) {
        WeatherAggregation aggregation = weatherAggregationRepository.aggregateByStationAndRange(stationCode, start, end);
        if (aggregation == null) {
            throw new EntityNotFoundException("No aggregated weather data found for station: " + stationCode + " in range " + start + " to " + end);
        }
        return weatherAggregationMapper.toDto(aggregation);
    }
}
