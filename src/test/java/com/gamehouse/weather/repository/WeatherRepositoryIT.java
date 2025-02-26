package com.gamehouse.weather.repository;

import com.gamehouse.weather.BaseIT;
import com.gamehouse.weather.model.Weather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class WeatherRepositoryIT extends BaseIT {

    @Autowired
    private WeatherRepository repository;

    @Test
    void testSaveAndFindById() {
        LocalDateTime now = LocalDateTime.now();
        Weather weather = new Weather();
        weather.setStationCode("ABC");
        weather.setCollectedAt(now.minusMinutes(5));
        weather.setReceivedAt(now);
        weather.setTemperature(25.5);
        weather.setHumidity(60.0);
        weather.setWindSpeed(1.3);

        Weather saved = repository.save(weather);
        assertThat(saved.getId()).isNotNull();

        Optional<Weather> foundOpt = repository.findById(saved.getId());
        assertThat(foundOpt).isPresent();

        Weather found = foundOpt.get();
        assertThat(found.getStationCode()).isEqualTo("ABC");
        assertThat(found.getCollectedAt()).isEqualTo(weather.getCollectedAt());
        assertThat(found.getReceivedAt()).isEqualTo(weather.getReceivedAt());
        assertThat(found.getTemperature()).isEqualTo(25.5);
        assertThat(found.getHumidity()).isEqualTo(60.0);
        assertThat(found.getWindSpeed()).isEqualTo(1.3);
    }

    @Test
    void testFindFirstByStationCodeOrderByReceivedAtDesc() {
        String stationCode = "ABC";
        LocalDateTime now = LocalDateTime.now();

        Weather weather1 = new Weather();
        weather1.setStationCode(stationCode);
        weather1.setCollectedAt(now.minusMinutes(10));
        weather1.setReceivedAt(now.minusMinutes(10));
        weather1.setTemperature(20.0);
        weather1.setHumidity(50.0);
        weather1.setWindSpeed(5.0);
        repository.save(weather1);

        Weather weather2 = new Weather();
        weather2.setStationCode(stationCode);
        weather2.setCollectedAt(now.minusMinutes(5));
        weather2.setReceivedAt(now.minusMinutes(5));
        weather2.setTemperature(22.0);
        weather2.setHumidity(55.0);
        weather2.setWindSpeed(6.0);
        repository.save(weather2);

        Weather weather3 = new Weather();
        weather3.setStationCode(stationCode);
        weather3.setCollectedAt(now.minusMinutes(1));
        weather3.setReceivedAt(now);
        weather3.setTemperature(24.0);
        weather3.setHumidity(60.0);
        weather3.setWindSpeed(7.0);
        repository.save(weather3);

        Optional<Weather> lastOpt = repository.findFirstByStationCodeOrderByReceivedAtDesc(stationCode);

        assertThat(lastOpt).isPresent();
        Weather last = lastOpt.get();
        assertThat(last.getReceivedAt()).isEqualTo(now);
        assertThat(last.getTemperature()).isEqualTo(24.0);
    }
}
