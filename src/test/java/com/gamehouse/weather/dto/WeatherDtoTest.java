package com.gamehouse.weather.dto;

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

public class WeatherDtoTest extends BaseTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validWeatherDto_ShouldHaveNoViolations() {
        WeatherDto dto = new WeatherDto(1L, "ABC", LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now(), 25.5, 55.0, 12.3);

        Set<ConstraintViolation<WeatherDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABCD"})
    void invalidStationCode_ShouldReturnViolation(String code) {
        WeatherDto dto = new WeatherDto(1L, code, LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now(), 25.5, 55.0, 12.3);

        Set<ConstraintViolation<WeatherDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("stationCode"));
    }

    @Test
    void missingFields_ShouldReturnViolations() {
        WeatherDto dto = new WeatherDto(null, null, null, null, null, null, null);

        Set<ConstraintViolation<WeatherDto>> violations = validator.validate(dto);
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
        WeatherDto dto = new WeatherDto(1L, "ABC", LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now(), 25.5, 55.0, windSpeed);

        Set<ConstraintViolation<WeatherDto>> violations = validator.validate(dto);
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
        WeatherDto dto = new WeatherDto(1L, "ABC", LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now(), 25.0, humidity, 10.0);

        Set<ConstraintViolation<WeatherDto>> violations = validator.validate(dto);
        assertThat(violations.isEmpty()).isEqualTo(isValid);
    }
}
