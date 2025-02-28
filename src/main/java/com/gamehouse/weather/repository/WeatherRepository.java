package com.gamehouse.weather.repository;

import com.gamehouse.weather.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {
    Optional<Weather> findFirstByStationCodeOrderByReceivedAtDesc(String stationCode);

    @Query("SELECT DISTINCT w.stationCode FROM Weather w")
    List<String> findAllDistinctStationCodes();

    @Query(value = "SELECT AVG(temperature) FROM weather " +
            "WHERE station_code = :stationCode AND collected_at BETWEEN :start AND :end",
            nativeQuery = true)
    Double findStationAverageTemperatureBetween(@Param("stationCode") String stationCode,
                                                @Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);
}
