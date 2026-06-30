CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE telemetry (
    id         BIGSERIAL          PRIMARY KEY,
    vehicle_id VARCHAR(64)        NOT NULL,
    region     VARCHAR(64),
    lat        DOUBLE PRECISION   NOT NULL,
    lng        DOUBLE PRECISION   NOT NULL,
    speed      DOUBLE PRECISION,
    heading    DOUBLE PRECISION,
    ts         TIMESTAMPTZ        NOT NULL,
    location   GEOGRAPHY(POINT, 4326) GENERATED ALWAYS AS
               (ST_SetSRID(ST_MakePoint(lng, lat), 4326)) STORED
);
