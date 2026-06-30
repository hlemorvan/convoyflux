// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.infrastructure.reactive;

import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.port.out.TelemetryBroadcast;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Pont Reactor pour la diffusion temps réel.
 *
 * directBestEffort() : chaque abonné reçoit les messages de manière
 * best-effort — si un abonné SSE est lent, ses messages sont droppés
 * individuellement sans bloquer les autres ni le producteur.
 * Cela implémente le comportement "onBackpressureLatest" au niveau
 * de chaque subscriber SSE (positions périmées sacrifiées, plus fraîche retenue).
 */
@Component
public class TelemetrySinkBroadcast implements TelemetryBroadcast {

    private final Sinks.Many<Telemetry> sink =
            Sinks.many().multicast().directBestEffort();

    @Override
    public void publish(Telemetry telemetry) {
        sink.tryEmitNext(telemetry);
    }

    @Override
    public Flux<Telemetry> asFlux() {
        return sink.asFlux();
    }
}
