// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.infrastructure.reactive;

import com.convoyflux.domain.model.GeoPoint;
import com.convoyflux.domain.model.Region;
import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.model.VehicleId;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Instant;

class TelemetrySinkBroadcastTest {

    private static Telemetry telemetry(String vehicleId) {
        return new Telemetry(
                new VehicleId(vehicleId),
                new Region("ile-de-france"),
                new GeoPoint(48.8566, 2.3522),
                50.0, 180.0,
                Instant.now()
        );
    }

    @Test
    void published_telemetry_is_received_by_subscriber() {
        var broadcast = new TelemetrySinkBroadcast();
        var t = telemetry("v-001");

        StepVerifier.create(broadcast.asFlux().take(1))
                .then(() -> broadcast.publish(t))
                .expectNext(t)
                .verifyComplete();
    }

    @Test
    void multiple_subscribers_each_receive_the_same_event() {
        var broadcast = new TelemetrySinkBroadcast();
        var t = telemetry("v-002");

        var flux1 = broadcast.asFlux().take(1);
        var flux2 = broadcast.asFlux().take(1);

        StepVerifier.create(flux1)
                .then(() -> broadcast.publish(t))
                .expectNext(t)
                .verifyComplete();

        StepVerifier.create(flux2)
                .then(() -> broadcast.publish(t))
                .expectNext(t)
                .verifyComplete();
    }
}
