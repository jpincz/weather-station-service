package com.gamehouse.weather.service;

import com.gamehouse.weather.dto.WeatherAggregationResponse;
import com.gamehouse.weather.dto.WeatherRequest;
import com.gamehouse.weather.dto.WeatherResponse;
import com.gamehouse.weather.dto.mapper.WeatherAggregationMapper;
import com.gamehouse.weather.dto.mapper.WeatherMapper;
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
    public WeatherResponse save(WeatherRequest request) {
        LocalDateTime currentDate = LocalDateTime.now();
        if (request.getCollectedAt().isAfter(currentDate)) {
            throw new IllegalArgumentException("Collected time must not be in the future.");
        }

        Weather entity = mapper.toEntity(request);
        entity.setReceivedAt(LocalDateTime.now());
        Weather savedEntity = repository.save(entity);
        return mapper.toDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public WeatherResponse getLastByStation(String stationCode) {
        Weather weather = repository.findFirstByStationCodeOrderByReceivedAtDesc(stationCode)
                .orElseThrow(() -> new EntityNotFoundException("No weather entry found for station: " + stationCode));
        return mapper.toDto(weather);
    }

    @Transactional(readOnly = true)
    public WeatherAggregationResponse getAggregationByStationAndDateRange(String stationCode, LocalDateTime start, LocalDateTime end) {
        WeatherAggregation aggregation = weatherAggregationRepository.aggregateByStationAndRange(stationCode, start, end);
        if (aggregation == null) {
            throw new EntityNotFoundException("No aggregated weather data found for station: " + stationCode + " in range " + start + " to " + end);
        }
        return weatherAggregationMapper.toDto(aggregation);
    }
}
