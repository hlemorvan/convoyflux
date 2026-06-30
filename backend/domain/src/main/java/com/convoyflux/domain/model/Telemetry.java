// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.domain.model;

import java.time.Instant;

public record Telemetry(
        VehicleId vehicleId,
        Region region,
        GeoPoint position,
        double speed,
        double heading,
        Instant timestamp
) {
    public Telemetry {
        if (vehicleId == null)  throw new IllegalArgumentException("vehicleId is required");
        if (region == null)     throw new IllegalArgumentException("region is required");
        if (position == null)   throw new IllegalArgumentException("position is required");
        if (timestamp == null)  throw new IllegalArgumentException("timestamp is required");
        if (speed < 0)          throw new IllegalArgumentException("speed must be >= 0, got: " + speed);
        if (heading < 0 || heading > 360) {
            throw new IllegalArgumentException("heading must be in [0, 360], got: " + heading);
        }
    }
}
