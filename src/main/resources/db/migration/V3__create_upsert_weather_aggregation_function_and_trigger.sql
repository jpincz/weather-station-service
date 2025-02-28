CREATE
OR REPLACE FUNCTION upsert_weather_aggregation(
    p_station_code VARCHAR,
    p_collected_at TIMESTAMP
)
RETURNS VOID AS $$

BEGIN

INSERT INTO weather_aggregation (station_code,
                                 minute_window,
                                 total_records,
                                 avg_temperature,
                                 min_temperature,
                                 max_temperature,
                                 avg_humidity,
                                 min_humidity,
                                 max_humidity,
                                 avg_wind_speed,
                                 min_wind_speed,
                                 max_wind_speed)
SELECT w.station_code,
       date_trunc('minute', p_collected_at) AS minute_window,
       count(*)                             AS total_records,
       avg(w.temperature)                   AS avg_temperature,
       min(w.temperature)                   AS min_temperature,
       max(w.temperature)                   AS max_temperature,
       avg(w.humidity)                      AS avg_humidity,
       min(w.humidity)                      AS min_humidity,
       max(w.humidity)                      AS max_humidity,
       avg(w.wind_speed)                    AS avg_wind_speed,
       min(w.wind_speed)                    AS min_wind_speed,
       max(w.wind_speed)                    AS max_wind_speed

FROM weather w
WHERE w.station_code = p_station_code
  AND w.collected_at >= date_trunc('minute', p_collected_at)
  AND w.collected_at < date_trunc('minute', p_collected_at) + interval '1 minute'
GROUP BY w.station_code, minute_window

ON CONFLICT (station_code, minute_window) DO
UPDATE SET
    total_records = EXCLUDED.total_records,
    avg_temperature = EXCLUDED.avg_temperature,
    min_temperature = EXCLUDED.min_temperature,
    max_temperature = EXCLUDED.max_temperature,
    avg_humidity = EXCLUDED.avg_humidity,
    min_humidity = EXCLUDED.min_humidity,
    max_humidity = EXCLUDED.max_humidity,
    avg_wind_speed = EXCLUDED.avg_wind_speed,
    min_wind_speed = EXCLUDED.min_wind_speed,
    max_wind_speed = EXCLUDED.max_wind_speed;

END;

$$
LANGUAGE plpgsql;

CREATE
OR REPLACE FUNCTION trigger_upsert_weather_aggregation()
RETURNS trigger AS $$
BEGIN
    PERFORM
upsert_weather_aggregation(NEW.station_code, NEW.collected_at);
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_upsert_weather_aggregation ON weather;
CREATE TRIGGER trg_upsert_weather_aggregation
    AFTER INSERT
    ON weather
    FOR EACH ROW
    EXECUTE FUNCTION trigger_upsert_weather_aggregation();
