CREATE TABLE weather_aggregation
(
    station_code    VARCHAR(3)       NOT NULL,
    minute_window   TIMESTAMP        NOT NULL,
    total_records   INTEGER          NOT NULL,
    avg_temperature DOUBLE PRECISION NOT NULL,
    min_temperature DOUBLE PRECISION NOT NULL,
    max_temperature DOUBLE PRECISION NOT NULL,
    avg_humidity    DOUBLE PRECISION NOT NULL,
    min_humidity    DOUBLE PRECISION NOT NULL,
    max_humidity    DOUBLE PRECISION NOT NULL,
    avg_wind_speed  DOUBLE PRECISION NOT NULL,
    min_wind_speed  DOUBLE PRECISION NOT NULL,
    max_wind_speed  DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (station_code, minute_window)
);
