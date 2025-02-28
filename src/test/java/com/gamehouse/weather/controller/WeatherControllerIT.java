package com.gamehouse.weather.controller;

import com.gamehouse.weather.BaseIT;
import com.gamehouse.weather.dto.WeatherAggregationDto;
import com.gamehouse.weather.dto.WeatherDto;
import com.gamehouse.weather.repository.WeatherAggregationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class WeatherControllerIT extends BaseIT {

    private static final String WEATHER_ENTRIES_PATH = "/weather";

    @LocalServerPort
    private int port;

    @Autowired
    private WeatherAggregationRepository weatherAggregationRepository;

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

    private String buildAggregationUriForStationRange(String stationCode, LocalDateTime start, LocalDateTime end) {
        return UriComponentsBuilder.fromUriString(buildBaseUri())
                .pathSegment(stationCode, "range")
                .queryParam("start", start)
                .queryParam("end", end)
                .build()
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

    @Test
    void getAggregationByStationRange_ValidStation_ShouldReturnAggregatedData() {
        String stationCode = "ABC";
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        WeatherDto dto1 = new WeatherDto(null, stationCode, now.minusMinutes(15), now.minusMinutes(15), 20.0, 50.0, 5.0);
        WeatherDto dto2 = new WeatherDto(null, stationCode, now.minusMinutes(15), now.minusMinutes(15), 22.0, 55.0, 7.0);
        restTemplate.postForEntity(buildBaseUri(), dto1, WeatherDto.class);
        restTemplate.postForEntity(buildBaseUri(), dto2, WeatherDto.class);
        LocalDateTime start = now.minusMinutes(17);
        LocalDateTime end = now.minusMinutes(14);
        String aggregationUrl = buildAggregationUriForStationRange(stationCode, start, end);
        ResponseEntity<WeatherAggregationDto> response = restTemplate.getForEntity(aggregationUrl, WeatherAggregationDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        WeatherAggregationDto aggregationDto = response.getBody();
        assertThat(aggregationDto).isNotNull();
        double expectedAvgTemp = 21.0;
        double expectedMinTemp = 20.0;
        double expectedMaxTemp = 22.0;
        assertThat(aggregationDto.getTemperature().getAvg()).isCloseTo(expectedAvgTemp, within(0.1));
        assertThat(aggregationDto.getTemperature().getMin()).isEqualTo(expectedMinTemp);
        assertThat(aggregationDto.getTemperature().getMax()).isEqualTo(expectedMaxTemp);
        double expectedAvgHumidity = 52.5;
        double expectedMinHumidity = 50.0;
        double expectedMaxHumidity = 55.0;
        assertThat(aggregationDto.getHumidity().getAvg()).isCloseTo(expectedAvgHumidity, within(0.1));
        assertThat(aggregationDto.getHumidity().getMin()).isEqualTo(expectedMinHumidity);
        assertThat(aggregationDto.getHumidity().getMax()).isEqualTo(expectedMaxHumidity);
        double expectedAvgWind = 6.0;
        double expectedMinWind = 5.0;
        double expectedMaxWind = 7.0;
        assertThat(aggregationDto.getWindSpeed().getAvg()).isCloseTo(expectedAvgWind, within(0.1));
        assertThat(aggregationDto.getWindSpeed().getMin()).isEqualTo(expectedMinWind);
        assertThat(aggregationDto.getWindSpeed().getMax()).isEqualTo(expectedMaxWind);
    }

    @Test
    void getAggregationByStationRange_NoData_ShouldReturnNotFound() {
        String stationCode = "XYZ";
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDateTime start = now.minusMinutes(17);
        LocalDateTime end = now.minusMinutes(14);
        String aggregationUrl = buildAggregationUriForStationRange(stationCode, start, end);
        ResponseEntity<String> response = restTemplate.getForEntity(aggregationUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAggregationByStationRange_MultipleWindows_ShouldAggregateCorrectly() {
        String stationCode = "ABC";
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

        WeatherDto entry1 = new WeatherDto(
                null,
                stationCode,
                now.minusMinutes(20).plusSeconds(20),
                now.minusMinutes(20).plusSeconds(20),
                20.0,
                40.0,
                5.0
        );
        WeatherDto entry2 = new WeatherDto(
                null,
                stationCode,
                now.minusMinutes(19).plusSeconds(20),
                now.minusMinutes(19).plusSeconds(20),
                22.0,
                42.0,
                6.0
        );

        restTemplate.postForEntity(buildBaseUri(), entry1, WeatherDto.class);
        restTemplate.postForEntity(buildBaseUri(), entry2, WeatherDto.class);

        LocalDateTime start = now.minusMinutes(23);
        LocalDateTime end = now.minusMinutes(17);
        String aggregationUrl = buildAggregationUriForStationRange(stationCode, start, end);

        ResponseEntity<WeatherAggregationDto> response = restTemplate.getForEntity(aggregationUrl, WeatherAggregationDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        WeatherAggregationDto aggregationDto = response.getBody();
        assertThat(aggregationDto).isNotNull();

        assertThat(aggregationDto.getTemperature().getAvg()).isCloseTo(21.0, within(0.1));
        assertThat(aggregationDto.getTemperature().getMin()).isEqualTo(20.0);
        assertThat(aggregationDto.getTemperature().getMax()).isEqualTo(22.0);

        assertThat(aggregationDto.getHumidity().getAvg()).isCloseTo(41.0, within(0.1));
        assertThat(aggregationDto.getHumidity().getMin()).isEqualTo(40.0);
        assertThat(aggregationDto.getHumidity().getMax()).isEqualTo(42.0);

        assertThat(aggregationDto.getWindSpeed().getAvg()).isCloseTo(5.5, within(0.1));
        assertThat(aggregationDto.getWindSpeed().getMin()).isEqualTo(5.0);
        assertThat(aggregationDto.getWindSpeed().getMax()).isEqualTo(6.0);
    }
}
