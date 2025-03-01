package com.gamehouse.weather.service;

import com.gamehouse.weather.BaseIT;
import com.gamehouse.weather.model.Weather;
import com.gamehouse.weather.repository.WeatherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {
        "ALERT_TEMPERATURE_THRESHOLD=70.0",
        "alert.missing.data.window.seconds=30",
        "alert.temperature.window.seconds=30"
})
class WeatherAlertServiceIT extends BaseIT {

    private final PrintStream originalOut = System.out;
    @Autowired
    private WeatherAlertService weatherAlertService;
    @Autowired
    private WeatherRepository weatherRepository;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        weatherRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private Weather createDefaultWeather(String stationCode, OffsetDateTime collectedAt, OffsetDateTime receivedAt,
                                         double temperature, double humidity, double windSpeed) {
        Weather w = new Weather();
        w.setStationCode(stationCode);
        w.setCollectedAt(collectedAt);
        w.setReceivedAt(receivedAt);
        w.setTemperature(temperature);
        w.setHumidity(humidity);
        w.setWindSpeed(windSpeed);
        return w;
    }

    @Test
    void testAlertMissingDataIntegration() {
        OffsetDateTime now = OffsetDateTime.now();
        Weather w1 = createDefaultWeather("ABC", now.minusSeconds(31), now.minusSeconds(31), 20.0, 50.0, 5.0);
        weatherRepository.save(w1);
        Weather w2 = createDefaultWeather("XYZ", now.minusSeconds(10), now.minusSeconds(10), 22.0, 55.0, 6.0);
        weatherRepository.save(w2);
        weatherAlertService.alertMissingData();
        String output = outContent.toString();
        assertThat(output).contains("ALERT Missing data from stations:");
        assertThat(output).contains("ABC");
        assertThat(output).doesNotContain("XYZ");
    }

    @Test
    void testAlertTemperatureThresholdIntegration() {
        OffsetDateTime now = OffsetDateTime.now();
        Weather w1 = createDefaultWeather("AAA", now.minusSeconds(10), now.minusSeconds(10), 72.2, 50.0, 5.0);
        weatherRepository.save(w1);
        Weather w2 = createDefaultWeather("BBB", now.minusSeconds(10), now.minusSeconds(10), 68.0, 55.0, 6.0);
        weatherRepository.save(w2);
        weatherAlertService.alertTemperatureThreshold();
        String output = outContent.toString();
        assertThat(output).contains("ALERT Station AAA had a 30-second average");
        assertThat(output).doesNotContain("BBB");
    }
}
