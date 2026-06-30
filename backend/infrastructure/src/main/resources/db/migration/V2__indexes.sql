CREATE INDEX idx_telemetry_vehicle_ts ON telemetry (vehicle_id, ts DESC);
CREATE INDEX idx_telemetry_location   ON telemetry USING GIST (location);
