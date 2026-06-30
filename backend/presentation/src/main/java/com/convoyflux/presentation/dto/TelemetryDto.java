// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.presentation.dto;

import com.convoyflux.domain.model.Telemetry;

import java.time.Instant;

public record TelemetryDto(
        String  vehicleId,
        String  region,
        double  lat,
        double  lng,
        double  speed,
        double  heading,
        Instant timestamp
) {
    public static TelemetryDto from(Telemetry t) {
        return new TelemetryDto(
                t.vehicleId().value(),
                t.region().name(),
                t.position().lat(),
                t.position().lng(),
                t.speed(),
                t.heading(),
                t.timestamp()
        );
    }
}
