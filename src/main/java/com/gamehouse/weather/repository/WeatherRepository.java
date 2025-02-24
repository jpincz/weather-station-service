package com.gamehouse.weather.repository;

import com.gamehouse.weather.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {
    Optional<Weather> findFirstByStationCodeOrderByReceivedAtDesc(String stationCode);
}
