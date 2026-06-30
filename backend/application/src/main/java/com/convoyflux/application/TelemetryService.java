// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.application;

import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.model.VehicleId;
import com.convoyflux.domain.port.in.IngestTelemetryUseCase;
import com.convoyflux.domain.port.in.QueryHistoryUseCase;
import com.convoyflux.domain.port.out.TelemetryBroadcast;
import com.convoyflux.domain.port.out.TelemetryRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TelemetryService implements IngestTelemetryUseCase, QueryHistoryUseCase {

    private final TelemetryRepository repository;
    private final TelemetryBroadcast broadcast;

    public TelemetryService(TelemetryRepository repository, TelemetryBroadcast broadcast) {
        this.repository = repository;
        this.broadcast = broadcast;
    }

    @Override
    public Mono<Void> ingest(Telemetry telemetry) {
        return repository.save(telemetry)
                .doOnSuccess(__ -> broadcast.publish(telemetry));
    }

    @Override
    public Flux<Telemetry> history(VehicleId vehicleId, int limit) {
        return repository.findByVehicleId(vehicleId, limit);
    }
}
