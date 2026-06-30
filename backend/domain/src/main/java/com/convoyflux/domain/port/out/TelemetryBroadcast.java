// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.domain.port.out;

import com.convoyflux.domain.model.Telemetry;
import reactor.core.publisher.Flux;

public interface TelemetryBroadcast {

    void publish(Telemetry telemetry);

    Flux<Telemetry> asFlux();
}
