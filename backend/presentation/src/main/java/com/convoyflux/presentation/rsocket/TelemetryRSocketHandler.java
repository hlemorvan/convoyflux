// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.presentation.rsocket;

import com.convoyflux.domain.port.out.TelemetryBroadcast;
import com.convoyflux.presentation.dto.TelemetryDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class TelemetryRSocketHandler {

    private final TelemetryBroadcast broadcast;

    public TelemetryRSocketHandler(TelemetryBroadcast broadcast) {
        this.broadcast = broadcast;
    }

    /** RSocket request-stream : le client s'abonne au flux temps réel de la flotte. */
    @MessageMapping("fleet.stream")
    public Flux<TelemetryDto> stream() {
        return broadcast.asFlux()
                .map(TelemetryDto::from);
    }
}
