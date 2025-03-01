package com.gamehouse.weather.controller;

import com.gamehouse.weather.BaseIT;
import com.gamehouse.weather.dto.WeatherAggregationResponse;
import com.gamehouse.weather.dto.WeatherRequest;
import com.gamehouse.weather.dto.WeatherResponse;
import com.gamehouse.weather.repository.WeatherAggregationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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

    private String buildAggregationUriForStationRange(String stationCode, OffsetDateTime start, OffsetDateTime end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String startStr = start.withOffsetSameInstant(ZoneOffset.UTC).format(formatter);
        String endStr = end.withOffsetSameInstant(ZoneOffset.UTC).format(formatter);

        return UriComponentsBuilder.fromUriString(buildBaseUri())
                .pathSegment(stationCode, "range")
                .queryParam("start", startStr)
                .queryParam("end", endStr)
                .build(true)
                .toUriString();
    }

    @Test
    void save_Valid_ShouldReturn201() {
        WeatherResponse validDto = new WeatherResponse(
                null,
                "ABC",
                OffsetDateTime.now().minusMinutes(10),
                OffsetDateTime.now(),
                25.0,
                50.0,
                10.0
        );

        ResponseEntity<WeatherResponse> response = restTemplate.postForEntity(
                buildBaseUri(), validDto, WeatherResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getStationCode()).isEqualTo("ABC");
    }

    @Test
    void save_Invalid_ShouldReturn400() {
        WeatherResponse invalidDto = new WeatherResponse(
                null,
                "ABC",
                OffsetDateTime.now().plusMinutes(10),
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
        WeatherRequest dto1 = new WeatherRequest(
                stationCode,
                OffsetDateTime.now().minusMinutes(20),
                22.0,
                55.0,
                10.0
        );
        WeatherRequest dto2 = new WeatherRequest(
                stationCode,
                OffsetDateTime.now(),
                24.0,
                50.0,
                12.0
        );

        restTemplate.postForEntity(buildBaseUri(), dto1, WeatherResponse.class);
        restTemplate.postForEntity(buildBaseUri(), dto2, WeatherResponse.class);

        String lastUrl = buildLatestWeatherUriForStationCode(stationCode);

        ResponseEntity<WeatherResponse> response = restTemplate.getForEntity(lastUrl, WeatherResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        WeatherResponse last = response.getBody();
        assertThat(last).isNotNull();

        assertThat(last.getId()).isNotNull();
        assertThat(last.getReceivedAt()).isNotNull();
        assertThat(last.getStationCode()).isEqualTo(dto2.getStationCode());
        assertThat(last.getCollectedAt()).isEqualTo(dto2.getCollectedAt());
        assertThat(last.getTemperature()).isEqualTo(dto2.getTemperature());
        assertThat(last.getHumidity()).isEqualTo(dto2.getHumidity());
        assertThat(last.getWindSpeed()).isEqualTo(dto2.getWindSpeed());
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
        OffsetDateTime now = OffsetDateTime.now().withSecond(0).withNano(0);
        WeatherResponse dto1 = new WeatherResponse(null, stationCode, now.minusMinutes(15), now.minusMinutes(15), 20.0, 50.0, 5.0);
        WeatherResponse dto2 = new WeatherResponse(null, stationCode, now.minusMinutes(15), now.minusMinutes(15), 22.0, 55.0, 7.0);
        restTemplate.postForEntity(buildBaseUri(), dto1, WeatherResponse.class);
        restTemplate.postForEntity(buildBaseUri(), dto2, WeatherResponse.class);
        OffsetDateTime start = now.minusMinutes(17);
        OffsetDateTime end = now.minusMinutes(14);
        String aggregationUrl = buildAggregationUriForStationRange(stationCode, start, end);
        ResponseEntity<WeatherAggregationResponse> response = restTemplate.getForEntity(aggregationUrl, WeatherAggregationResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        WeatherAggregationResponse aggregationDto = response.getBody();
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
        OffsetDateTime now = OffsetDateTime.now().withSecond(0).withNano(0);
        OffsetDateTime start = now.minusMinutes(17);
        OffsetDateTime end = now.minusMinutes(14);
        String aggregationUrl = buildAggregationUriForStationRange(stationCode, start, end);
        ResponseEntity<String> response = restTemplate.getForEntity(aggregationUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void getAggregationByStationRange_MultipleWindows_ShouldAggregateCorrectly() {
        String stationCode = "ABC";
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).withSecond(0).withNano(0);

        WeatherRequest entry1 = new WeatherRequest(
                stationCode,
                now.minusMinutes(20).plusSeconds(20),
                20.0,
                40.0,
                5.0
        );
        WeatherRequest entry2 = new WeatherRequest(
                stationCode,
                now.minusMinutes(19).plusSeconds(20),
                22.0,
                42.0,
                6.0
        );

        restTemplate.postForEntity(buildBaseUri(), entry1, WeatherResponse.class);
        restTemplate.postForEntity(buildBaseUri(), entry2, WeatherResponse.class);

        OffsetDateTime start = now.minusMinutes(23);
        OffsetDateTime end = now.minusMinutes(17);
        String aggregationUrl = buildAggregationUriForStationRange(stationCode, start, end);

        ResponseEntity<WeatherAggregationResponse> response = restTemplate.getForEntity(aggregationUrl, WeatherAggregationResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        WeatherAggregationResponse aggregationDto = response.getBody();
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
