// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.infrastructure.mqtt;

import com.convoyflux.domain.model.GeoPoint;
import com.convoyflux.domain.model.Region;
import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.model.VehicleId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class TelemetryJsonDecoder {

    private final ObjectMapper objectMapper;

    public TelemetryJsonDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Telemetry decode(byte[] payload) throws IOException {
        Payload p = objectMapper.readValue(payload, Payload.class);
        return new Telemetry(
                new VehicleId(p.vehicleId),
                new Region(p.region),
                new GeoPoint(p.lat, p.lng),
                p.speed,
                p.heading,
                p.ts
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Payload {
        public String  vehicleId;
        public String  region;
        public double  lat;
        public double  lng;
        public double  speed;
        public double  heading;
        public Instant ts;
    }
}
