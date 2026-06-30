// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.domain.port.in;

import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.model.VehicleId;
import reactor.core.publisher.Flux;

public interface QueryHistoryUseCase {

    Flux<Telemetry> history(VehicleId vehicleId, int limit);
}
