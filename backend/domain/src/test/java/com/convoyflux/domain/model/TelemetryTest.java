// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TelemetryTest {

    private static final VehicleId VEHICLE  = new VehicleId("v-001");
    private static final Region    REGION   = new Region("ile-de-france");
    private static final GeoPoint  POSITION = new GeoPoint(48.8566, 2.3522);
    private static final Instant   NOW      = Instant.now();

    @Test
    void valid_telemetry_is_created() {
        var t = new Telemetry(VEHICLE, REGION, POSITION, 60.0, 90.0, NOW);
        assertEquals(VEHICLE, t.vehicleId());
        assertEquals(60.0, t.speed());
        assertEquals(90.0, t.heading());
    }

    @Test
    void null_vehicleId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Telemetry(null, REGION, POSITION, 0, 0, NOW));
    }

    @Test
    void null_region_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Telemetry(VEHICLE, null, POSITION, 0, 0, NOW));
    }

    @Test
    void null_position_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Telemetry(VEHICLE, REGION, null, 0, 0, NOW));
    }

    @Test
    void null_timestamp_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Telemetry(VEHICLE, REGION, POSITION, 0, 0, null));
    }

    @Test
    void negative_speed_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Telemetry(VEHICLE, REGION, POSITION, -1, 0, NOW));
    }

    @Test
    void heading_out_of_range_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Telemetry(VEHICLE, REGION, POSITION, 0, 361, NOW));
        assertThrows(IllegalArgumentException.class,
                () -> new Telemetry(VEHICLE, REGION, POSITION, 0, -1, NOW));
    }

    @Test
    void boundary_heading_values_accepted() {
        assertDoesNotThrow(() -> new Telemetry(VEHICLE, REGION, POSITION, 0, 0, NOW));
        assertDoesNotThrow(() -> new Telemetry(VEHICLE, REGION, POSITION, 0, 360, NOW));
    }
}
