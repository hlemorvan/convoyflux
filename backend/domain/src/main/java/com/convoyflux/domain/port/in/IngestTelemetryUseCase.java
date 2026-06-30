// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.domain.port.in;

import com.convoyflux.domain.model.Telemetry;
import reactor.core.publisher.Mono;

public interface IngestTelemetryUseCase {

    Mono<Void> ingest(Telemetry telemetry);
}
