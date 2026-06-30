// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.infrastructure.r2dbc;

import com.convoyflux.domain.model.GeoPoint;
import com.convoyflux.domain.model.Region;
import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.model.VehicleId;
import com.convoyflux.domain.port.out.TelemetryRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TelemetryRepositoryAdapter implements TelemetryRepository {

    private final TelemetryR2dbcRepo repo;

    public TelemetryRepositoryAdapter(TelemetryR2dbcRepo repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Void> save(Telemetry telemetry) {
        return repo.save(toEntity(telemetry)).then();
    }

    @Override
    public Flux<Telemetry> findByVehicleId(VehicleId vehicleId, int limit) {
        return repo.findByVehicleIdOrderByTsDesc(vehicleId.value(), limit)
                   .map(this::toDomain);
    }

    private TelemetryEntity toEntity(Telemetry t) {
        return new TelemetryEntity(
                t.vehicleId().value(),
                t.region().name(),
                t.position().lat(),
                t.position().lng(),
                t.speed(),
                t.heading(),
                t.timestamp()
        );
    }

    private Telemetry toDomain(TelemetryEntity e) {
        return new Telemetry(
                new VehicleId(e.getVehicleId()),
                new Region(e.getRegion()),
                new GeoPoint(e.getLat(), e.getLng()),
                e.getSpeed(),
                e.getHeading(),
                e.getTs()
        );
    }
}
