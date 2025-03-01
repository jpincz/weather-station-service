package com.gamehouse.weather.controller;

import com.gamehouse.weather.dto.WeatherAggregationResponse;
import com.gamehouse.weather.dto.WeatherRequest;
import com.gamehouse.weather.dto.WeatherResponse;
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
    public WeatherResponse save(@RequestBody @Valid WeatherRequest request) {
        return service.save(request);
    }

    @GetMapping("/{stationCode}/last")
    public WeatherResponse getLastByStation(@PathVariable String stationCode) {
        return service.getLastByStation(stationCode);
    }

    @GetMapping("/{stationCode}/range")
    public WeatherAggregationResponse getAggregationByStationAndDateRange(
            @PathVariable String stationCode,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        return service.getAggregationByStationAndDateRange(stationCode, startDate, endDate);
    }

}
