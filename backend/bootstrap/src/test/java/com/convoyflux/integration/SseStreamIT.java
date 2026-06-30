// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.integration;

import com.convoyflux.ConvoyFluxApplication;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;

@SpringBootTest(classes = ConvoyFluxApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SseStreamIT extends AbstractIntegrationTest {

    @LocalServerPort int port;
    @Autowired TelemetryBroadcast broadcast;

    @Test
    void sse_endpoint_streams_published_telemetry() {
        var telemetry = new Telemetry(
                new VehicleId("sse-v001"), new Region("ile-de-france"),
                new GeoPoint(48.8566, 2.3522), 60.0, 90.0, Instant.now()
        );

        // Use WebClient (not WebTestClient) for proper SSE streaming
        Flux<String> sseFlux = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build()
                .get()
                .uri("/api/stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .take(1);

        StepVerifier.create(sseFlux)
                .then(() -> {
                    // Brief pause so the SSE controller can subscribe to broadcast.asFlux()
                    // before we emit (directBestEffort drops events with no ready subscriber)
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                    broadcast.publish(telemetry);
                })
                .assertNext(data -> {
                    assert data != null && !data.isBlank();
                })
                .expectComplete()
                .verify(Duration.ofSeconds(10));
    }
}
