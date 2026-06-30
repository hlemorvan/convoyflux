// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.integration;

import com.convoyflux.domain.model.GeoPoint;
import com.convoyflux.domain.model.Region;
import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.model.VehicleId;
import com.convoyflux.domain.port.out.TelemetryBroadcast;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SseStreamIT extends AbstractIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TelemetryBroadcast broadcast;

    @Test
    void sse_endpoint_streams_published_telemetry() {
        var telemetry = new Telemetry(
                new VehicleId("sse-v001"), new Region("ile-de-france"),
                new GeoPoint(48.8566, 2.3522), 60.0, 90.0, Instant.now()
        );

        var client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .responseTimeout(Duration.ofSeconds(5))
                .build();

        var flux = client.get()
                .uri("/api/stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ServerSentEvent.class)
                .getResponseBody()
                .take(1);

        StepVerifier.create(flux)
                .then(() -> broadcast.publish(telemetry))
                .assertNext(sse -> {
                    // Vérifie qu'on reçoit bien un événement SSE
                    assert sse != null;
                })
                .verifyComplete();
    }
}
