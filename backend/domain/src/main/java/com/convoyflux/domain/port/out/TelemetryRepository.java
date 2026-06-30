// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.domain.port.out;

import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.model.VehicleId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TelemetryRepository {

    Mono<Void> save(Telemetry telemetry);

    Flux<Telemetry> findByVehicleId(VehicleId vehicleId, int limit);
}
