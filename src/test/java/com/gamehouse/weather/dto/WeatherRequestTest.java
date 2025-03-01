package com.gamehouse.weather.dto;

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

public class WeatherRequestTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validWeatherDto_ShouldHaveNoViolations() {
        WeatherRequest dto = new WeatherRequest("ABC",
                LocalDateTime.now(), 25.5, 55.0, 12.3);

        Set<ConstraintViolation<WeatherRequest>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABCD"})
    void invalidStationCode_ShouldReturnViolation(String code) {
        WeatherRequest dto = new WeatherRequest(code,
                LocalDateTime.now(), 25.5, 55.0, 12.3);

        Set<ConstraintViolation<WeatherRequest>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("stationCode"));
    }

    @Test
    void missingFields_ShouldReturnViolations() {
        WeatherRequest dto = new WeatherRequest(null, null, null, null, null);

        Set<ConstraintViolation<WeatherRequest>> violations = validator.validate(dto);
        assertThat(violations).hasSize(5);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactlyInAnyOrder(
                        "stationCode",
                        "collectedAt",
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
        WeatherRequest dto = new WeatherRequest("ABC",
                LocalDateTime.now(), 25.5, 55.0, windSpeed);

        Set<ConstraintViolation<WeatherRequest>> violations = validator.validate(dto);
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
        WeatherRequest dto = new WeatherRequest("ABC",
                LocalDateTime.now(), 25.0, humidity, 10.0);

        Set<ConstraintViolation<WeatherRequest>> violations = validator.validate(dto);
        assertThat(violations.isEmpty()).isEqualTo(isValid);
    }
}
