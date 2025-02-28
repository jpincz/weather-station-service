package com.gamehouse.weather.repository;

import com.gamehouse.weather.BaseIT;
import com.gamehouse.weather.model.WeatherAggregation;
import com.gamehouse.weather.model.WeatherAggregationId;
import com.gamehouse.weather.model.WeatherAggregationStats;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class WeatherAggregationRepositoryIT extends BaseIT {

    @Autowired
    private WeatherAggregationRepository repository;

    @Test
    void testSaveAndFindById() {
        LocalDateTime time = LocalDateTime.now().withSecond(0).withNano(0);
        WeatherAggregation aggregation = new WeatherAggregation(
                "ABC",
                time,
                150L,
                new WeatherAggregationStats(25.0, 25.0, 25.0),
                new WeatherAggregationStats(60.0, 60.0, 60.0),
                new WeatherAggregationStats(7.0, 7.0, 7.0)
        );
        WeatherAggregation saved = repository.save(aggregation);
        WeatherAggregationId id = new WeatherAggregationId("ABC", time);
        Optional<WeatherAggregation> foundOpt = repository.findById(id);
        assertThat(foundOpt).isPresent();
        WeatherAggregation found = foundOpt.get();
        assertThat(found.getStationCode()).isEqualTo("ABC");
        assertThat(found.getMinuteWindow()).isEqualTo(time);
        assertThat(found.getTotalRecords()).isEqualTo(150L);
        assertThat(found.getTemperature().getAvg()).isEqualTo(25.0);
        assertThat(found.getTemperature().getMin()).isEqualTo(25.0);
        assertThat(found.getTemperature().getMax()).isEqualTo(25.0);
        assertThat(found.getHumidity().getAvg()).isEqualTo(60.0);
        assertThat(found.getHumidity().getMin()).isEqualTo(60.0);
        assertThat(found.getHumidity().getMax()).isEqualTo(60.0);
        assertThat(found.getWindSpeed().getAvg()).isEqualTo(7.0);
        assertThat(found.getWindSpeed().getMin()).isEqualTo(7.0);
        assertThat(found.getWindSpeed().getMax()).isEqualTo(7.0);
    }

    @Test
    void testAggregateByStationRange() {
        String stationCode = "ABC";
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDateTime time1 = now.minusMinutes(10);
        LocalDateTime time2 = now.minusMinutes(5);
        WeatherAggregation agg1 = new WeatherAggregation(
                stationCode,
                time1,
                100L,
                new WeatherAggregationStats(20.0, 20.0, 20.0),
                new WeatherAggregationStats(50.0, 50.0, 50.0),
                new WeatherAggregationStats(5.0, 5.0, 5.0)
        );
        WeatherAggregation agg2 = new WeatherAggregation(
                stationCode,
                time2,
                200L,
                new WeatherAggregationStats(30.0, 30.0, 30.0),
                new WeatherAggregationStats(70.0, 70.0, 70.0),
                new WeatherAggregationStats(10.0, 10.0, 10.0)
        );
        repository.save(agg1);
        repository.save(agg2);
        LocalDateTime start = now.minusMinutes(12);
        LocalDateTime end = now.minusMinutes(3);
        WeatherAggregation aggregated = repository.aggregateByStationAndRange(stationCode, start, end);
        assertThat(aggregated).isNotNull();
        assertThat(aggregated.getStationCode()).isEqualTo(stationCode);
        assertThat(aggregated.getTotalRecords()).isEqualTo(300L);
        assertThat(aggregated.getTemperature().getAvg()).isCloseTo(25.0, within(0.1));
        assertThat(aggregated.getTemperature().getMin()).isEqualTo(20.0);
        assertThat(aggregated.getTemperature().getMax()).isEqualTo(30.0);
        assertThat(aggregated.getHumidity().getAvg()).isCloseTo(60.0, within(0.1));
        assertThat(aggregated.getHumidity().getMin()).isEqualTo(50.0);
        assertThat(aggregated.getHumidity().getMax()).isEqualTo(70.0);
        assertThat(aggregated.getWindSpeed().getAvg()).isCloseTo(7.5, within(0.1));
        assertThat(aggregated.getWindSpeed().getMin()).isEqualTo(5.0);
        assertThat(aggregated.getWindSpeed().getMax()).isEqualTo(10.0);
    }

    @Test
    void testAggregateByStationAndRangeQuery() {
        String stationCode = "ABC";
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        WeatherAggregation agg1 = new WeatherAggregation(
                stationCode,
                now.minusMinutes(10),
                100L,
                new WeatherAggregationStats(20.0, 20.0, 20.0),
                new WeatherAggregationStats(50.0, 50.0, 50.0),
                new WeatherAggregationStats(5.0, 5.0, 5.0)
        );
        WeatherAggregation agg2 = new WeatherAggregation(
                stationCode,
                now.minusMinutes(5),
                200L,
                new WeatherAggregationStats(30.0, 30.0, 30.0),
                new WeatherAggregationStats(70.0, 70.0, 70.0),
                new WeatherAggregationStats(10.0, 10.0, 10.0)
        );
        repository.save(agg1);
        repository.save(agg2);
        LocalDateTime start = now.minusMinutes(12);
        LocalDateTime end = now.minusMinutes(3);
        WeatherAggregation result = repository.aggregateByStationAndRange(stationCode, start, end);
        assertThat(result).isNotNull();
        assertThat(result.getStationCode()).isEqualTo(stationCode);
        assertThat(result.getTotalRecords()).isEqualTo(300L);
        assertThat(result.getTemperature().getAvg()).isCloseTo(25.0, within(0.1));
        assertThat(result.getTemperature().getMin()).isEqualTo(20.0);
        assertThat(result.getTemperature().getMax()).isEqualTo(30.0);
        assertThat(result.getHumidity().getAvg()).isCloseTo(60.0, within(0.1));
        assertThat(result.getHumidity().getMin()).isEqualTo(50.0);
        assertThat(result.getHumidity().getMax()).isEqualTo(70.0);
        assertThat(result.getWindSpeed().getAvg()).isCloseTo(7.5, within(0.1));
        assertThat(result.getWindSpeed().getMin()).isEqualTo(5.0);
        assertThat(result.getWindSpeed().getMax()).isEqualTo(10.0);
    }
}
