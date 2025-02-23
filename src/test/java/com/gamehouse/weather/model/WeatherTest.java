package com.gamehouse.weather.model;

import com.gamehouse.weather.BaseTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class WeatherTest extends BaseTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validWeather_ShouldHaveNoViolations() {
        Weather weather = new Weather();
        weather.setStationCode("ABC");
        weather.setCollectedAt(LocalDateTime.now().minusMinutes(5));
        weather.setReceivedAt(LocalDateTime.now());
        weather.setTemperature(25.5);
        weather.setHumidity(55.0);
        weather.setWindSpeed(12.3);

        Set<ConstraintViolation<Weather>> violations = validator.validate(weather);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABCD"})
    void invalidStationCode_ShouldReturnViolation(String code) {
        Weather weather = new Weather();
        weather.setStationCode(code);
        weather.setCollectedAt(LocalDateTime.now().minusMinutes(5));
        weather.setReceivedAt(LocalDateTime.now());
        weather.setTemperature(25.5);
        weather.setHumidity(55.0);
        weather.setWindSpeed(12.3);

        Set<ConstraintViolation<Weather>> violations = validator.validate(weather);
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("stationCode"));
    }

    @Test
    void missingFields_ShouldReturnViolations() {
        Weather weather = new Weather();

        Set<ConstraintViolation<Weather>> violations = validator.validate(weather);
        assertThat(violations).hasSize(6);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactlyInAnyOrder(
                        "stationCode",
                        "collectedAt",
                        "receivedAt",
                        "temperature",
                        "humidity",
                        "windSpeed"
                );
    }

    @ParameterizedTest
    @CsvSource({
            "0.0, true",
            "-0.1, false",
            "500.0, true",
            "1000.0, true",
            "1001.0, true"
    })
    void testWindSpeedValidation(double windSpeed, boolean isValid) {
        Weather weather = new Weather();
        weather.setStationCode("ABC");
        weather.setCollectedAt(LocalDateTime.now().minusMinutes(5));
        weather.setReceivedAt(LocalDateTime.now());
        weather.setTemperature(25.5);
        weather.setHumidity(55.0);
        weather.setWindSpeed(windSpeed);

        Set<ConstraintViolation<Weather>> violations = validator.validate(weather);
        assertThat(violations.isEmpty()).isEqualTo(isValid);
    }

    @ParameterizedTest
    @CsvSource({
            "0.0, true",
            "-0.1, false",
            "50.0, true",
            "100.0, true",
            "100.1, false"
    })
    void testHumidityValidation(double humidity, boolean isValid) {
        Weather weather = new Weather();
        weather.setStationCode("ABC");
        weather.setCollectedAt(LocalDateTime.now().minusMinutes(5));
        weather.setReceivedAt(LocalDateTime.now());
        weather.setTemperature(25.0);
        weather.setHumidity(humidity);
        weather.setWindSpeed(10.0);

        Set<ConstraintViolation<Weather>> violations = validator.validate(weather);
        assertThat(violations.isEmpty()).isEqualTo(isValid);
    }

}
