CREATE INDEX idx_weather_station_collected_at
    ON weather (station_code, collected_at);
