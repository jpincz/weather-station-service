package com.gamehouse.weather.repository;

import com.gamehouse.weather.model.WeatherAggregation;
import com.gamehouse.weather.model.WeatherAggregationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface WeatherAggregationRepository extends JpaRepository<WeatherAggregation, WeatherAggregationId> {

    @Query("SELECT new com.gamehouse.weather.model.WeatherAggregation(" +
            "    w.stationCode, " +
            "    null, " +
            "    SUM(w.totalRecords), " +
            "    new com.gamehouse.weather.model.WeatherAggregationStats(AVG(w.temperature.avg), MIN(w.temperature.min), MAX(w.temperature.max)), " +
            "    new com.gamehouse.weather.model.WeatherAggregationStats(AVG(w.humidity.avg), MIN(w.humidity.min), MAX(w.humidity.max)), " +
            "    new com.gamehouse.weather.model.WeatherAggregationStats(AVG(w.windSpeed.avg), MIN(w.windSpeed.min), MAX(w.windSpeed.max))" +
            ") " +
            "FROM WeatherAggregation w " +
            "WHERE w.stationCode = :stationCode " +
            "  AND w.minuteWindow BETWEEN :start AND :end " +
            "GROUP BY w.stationCode")
    WeatherAggregation aggregateByStationAndRange(@Param("stationCode") String stationCode,
                                                  @Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);
}
