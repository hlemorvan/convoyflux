// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.presentation.sse;

import com.convoyflux.domain.port.out.TelemetryBroadcast;
import com.convoyflux.presentation.dto.TelemetryDto;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class TelemetrySseController {

    private final TelemetryBroadcast broadcast;

    public TelemetrySseController(TelemetryBroadcast broadcast) {
        this.broadcast = broadcast;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<TelemetryDto>> stream() {
        return broadcast.asFlux()
                .map(t -> ServerSentEvent.<TelemetryDto>builder()
                        .event("telemetry")
                        .data(TelemetryDto.from(t))
                        .build());
    }
}
