package com.gamehouse.weather.service;

import com.gamehouse.weather.model.Weather;
import com.gamehouse.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WeatherAlertService {

    private static final String ALERT_TEMPERATURE_THRESHOLD_ENV_VARIABLE_NAME = "ALERT_TEMPERATURE_THRESHOLD";
    private static final String EVERY_TEN_SECONDS_CRON = "0/10 * * * * *";
    private static final String EVERY_THIRTY_SECONDS_CRON = "0,30 * * * * *";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Environment env;
    private final WeatherRepository weatherRepository;

    @Value("${alert.missing.data.window.seconds:30}")
    private long missingDataWindowSeconds;

    @Value("${alert.temperature.window.seconds:30}")
    private long temperatureWindowSeconds;

    @Scheduled(cron = EVERY_THIRTY_SECONDS_CRON)
    public void alertMissingData() {
        List<String> stationCodes = weatherRepository.findAllDistinctStationCodes();
        OffsetDateTime currentTime = OffsetDateTime.now();
        List<String> missingStations = new ArrayList<>();

        for (String stationCode : stationCodes) {
            Optional<Weather> latestEntry = weatherRepository.findFirstByStationCodeOrderByReceivedAtDesc(stationCode);
            if (latestEntry.isPresent() && latestEntry.get().getReceivedAt() != null &&
                    latestEntry.get().getReceivedAt().isBefore(currentTime.minusSeconds(missingDataWindowSeconds))) {
                missingStations.add(stationCode);
            }
        }

        if (!missingStations.isEmpty()) {
            System.out.println(FORMATTER.format(currentTime) + " ALERT Missing data from stations:");
            missingStations.forEach(code -> System.out.println("  - " + code));
        }
    }

    @Scheduled(cron = EVERY_TEN_SECONDS_CRON)
    public void alertTemperatureThreshold() {
        String thresholdProp = env.getProperty(ALERT_TEMPERATURE_THRESHOLD_ENV_VARIABLE_NAME);
        if (thresholdProp != null) {
            double temperatureThreshold = Double.parseDouble(thresholdProp);

            OffsetDateTime currentTime = OffsetDateTime.now();
            List<String> stationCodes = weatherRepository.findAllDistinctStationCodes();

            for (String stationCode : stationCodes) {
                Double averageTemperature = weatherRepository.findStationAverageTemperatureBetween(
                        stationCode,
                        currentTime.minusSeconds(temperatureWindowSeconds),
                        currentTime
                );
                if (averageTemperature != null && averageTemperature > temperatureThreshold) {
                    System.out.println(FORMATTER.format(currentTime) + " ALERT Station " + stationCode
                            + " had a 30-second average of " + averageTemperature + "ºC");
                }
            }
        }
    }
}
