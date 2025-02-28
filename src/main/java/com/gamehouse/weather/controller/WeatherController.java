package com.gamehouse.weather.controller;

import com.gamehouse.weather.dto.WeatherAggregationDto;
import com.gamehouse.weather.dto.WeatherDto;
import com.gamehouse.weather.service.WeatherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WeatherDto save(@RequestBody @Valid WeatherDto dto) {
        return service.save(dto);
    }

    @GetMapping("/{stationCode}/last")
    public WeatherDto getLastByStation(@PathVariable String stationCode) {
        return service.getLastByStation(stationCode);
    }

    @GetMapping("/{stationCode}/range")
    public WeatherAggregationDto getAggregationByStationAndDateRange(
            @PathVariable String stationCode,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        return service.getAggregationByStationAndDateRange(stationCode, startDate, endDate);
    }

}
