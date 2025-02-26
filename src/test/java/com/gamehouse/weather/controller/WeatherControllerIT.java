package com.gamehouse.weather.controller;

import com.gamehouse.weather.BaseIT;
import com.gamehouse.weather.dto.WeatherDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WeatherControllerIT extends BaseIT {

    private static final String WEATHER_ENTRIES_PATH = "/weather";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String buildBaseUri() {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path(WEATHER_ENTRIES_PATH)
                .build()
                .toUriString();
    }

    private String buildLatestWeatherUriForStationCode(String stationCode) {
        return UriComponentsBuilder.fromUriString(buildBaseUri())
                .pathSegment(stationCode, "last")
                .toUriString();
    }

    @Test
    void save_Valid_ShouldReturn201() {
        WeatherDto validDto = new WeatherDto(
                null,
                "ABC",
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now(),
                25.0,
                50.0,
                10.0
        );

        ResponseEntity<WeatherDto> response = restTemplate.postForEntity(
                buildBaseUri(), validDto, WeatherDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getStationCode()).isEqualTo("ABC");
    }

    @Test
    void save_Invalid_ShouldReturn400() {
        WeatherDto invalidDto = new WeatherDto(
                null,
                "ABC",
                LocalDateTime.now().plusMinutes(10),
                null,
                25.0,
                50.0,
                10.0
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                buildBaseUri(), invalidDto, String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Collected time must not be in the future.");
    }

    @Test
    void getLastByStation_ValidStation_ShouldReturn() {
        String stationCode = "ABC";
        WeatherDto dto1 = new WeatherDto(
                null,
                stationCode,
                LocalDateTime.now().minusMinutes(20),
                LocalDateTime.now().minusMinutes(15),
                22.0,
                55.0,
                10.0
        );
        WeatherDto dto2 = new WeatherDto(
                null,
                stationCode,
                LocalDateTime.now(),
                LocalDateTime.now(),
                24.0,
                50.0,
                12.0
        );

        restTemplate.postForEntity(buildBaseUri(), dto1, WeatherDto.class);
        restTemplate.postForEntity(buildBaseUri(), dto2, WeatherDto.class);

        String lastUrl = buildLatestWeatherUriForStationCode(stationCode);

        ResponseEntity<WeatherDto> response = restTemplate.getForEntity(lastUrl, WeatherDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        WeatherDto last = response.getBody();
        assertThat(last).isNotNull();

        dto2.setId(last.getId());
        dto2.setReceivedAt(last.getReceivedAt());
        assertThat(last).isEqualTo(dto2);
    }

    @Test
    void getLastByStation_InvalidStation_ShouldReturnNotFound() {
        String lastUrl = buildLatestWeatherUriForStationCode("XYZ");

        ResponseEntity<String> response = restTemplate.getForEntity(lastUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("No weather entry found for station: XYZ");
    }
}
