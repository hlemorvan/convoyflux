// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.infrastructure.r2dbc;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

interface TelemetryR2dbcRepo extends ReactiveCrudRepository<TelemetryEntity, Long> {

    // Colonnes explicites pour éviter de lire la colonne GENERATED 'location'
    @Query("""
            SELECT id, vehicle_id, region, lat, lng, speed, heading, ts
            FROM telemetry
            WHERE vehicle_id = :vehicleId
            ORDER BY ts DESC
            LIMIT :limit
            """)
    Flux<TelemetryEntity> findByVehicleIdOrderByTsDesc(String vehicleId, int limit);
}
