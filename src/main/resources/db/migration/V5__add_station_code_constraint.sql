ALTER TABLE weather
    ADD CONSTRAINT chk_station_code CHECK (station_code ~ '^[A-Z]{3}$');
