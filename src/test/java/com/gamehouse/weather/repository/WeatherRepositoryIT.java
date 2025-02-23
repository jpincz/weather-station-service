package com.gamehouse.weather.repository;

import com.gamehouse.weather.BaseTest;
import com.gamehouse.weather.model.Weather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WeatherRepositoryIT extends BaseTest {

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

}
