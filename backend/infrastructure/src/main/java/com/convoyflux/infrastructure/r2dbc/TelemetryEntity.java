// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.infrastructure.r2dbc;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("telemetry")
class TelemetryEntity {

    @Id
    private Long id;

    @Column("vehicle_id")
    private String vehicleId;

    @Column("region")
    private String region;

    @Column("lat")
    private double lat;

    @Column("lng")
    private double lng;

    @Column("speed")
    private double speed;

    @Column("heading")
    private double heading;

    @Column("ts")
    private Instant ts;

    // 'location' est une colonne GENERATED ALWAYS — non mappée ici

    TelemetryEntity() {}

    TelemetryEntity(String vehicleId, String region,
                    double lat, double lng,
                    double speed, double heading,
                    Instant ts) {
        this.vehicleId = vehicleId;
        this.region    = region;
        this.lat       = lat;
        this.lng       = lng;
        this.speed     = speed;
        this.heading   = heading;
        this.ts        = ts;
    }

    Long    getId()        { return id; }
    String  getVehicleId() { return vehicleId; }
    String  getRegion()    { return region; }
    double  getLat()       { return lat; }
    double  getLng()       { return lng; }
    double  getSpeed()     { return speed; }
    double  getHeading()   { return heading; }
    Instant getTs()        { return ts; }
}
