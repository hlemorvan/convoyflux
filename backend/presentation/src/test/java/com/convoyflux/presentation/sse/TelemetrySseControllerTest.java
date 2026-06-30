// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.presentation.sse;

import com.convoyflux.domain.model.GeoPoint;
import com.convoyflux.domain.model.Region;
import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.model.VehicleId;
import com.convoyflux.domain.port.out.TelemetryBroadcast;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelemetrySseControllerTest {

    @Mock TelemetryBroadcast broadcast;

    @Test
    void stream_maps_telemetry_to_sse() {
        var telemetry = new Telemetry(
                new VehicleId("v-001"), new Region("idf"),
                new GeoPoint(48.8566, 2.3522),
                60.0, 90.0, Instant.now()
        );
        when(broadcast.asFlux()).thenReturn(Flux.just(telemetry));

        var controller = new TelemetrySseController(broadcast);

        StepVerifier.create(controller.stream())
                .assertNext(sse -> {
                    assert sse.event() != null && sse.event().equals("telemetry");
                    assert sse.data() != null;
                    assert sse.data().vehicleId().equals("v-001");
                })
                .verifyComplete();
    }
}
