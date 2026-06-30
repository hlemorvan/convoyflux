// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.integration;

import com.convoyflux.domain.model.GeoPoint;
import com.convoyflux.domain.model.Region;
import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.model.VehicleId;
import com.convoyflux.domain.port.out.TelemetryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HistoryIT extends AbstractIntegrationTest {

    @LocalServerPort int port;
    @Autowired TelemetryRepository repository;

    @Test
    void history_endpoint_returns_persisted_positions() {
        var telemetry = new Telemetry(
                new VehicleId("hist-v001"), new Region("ile-de-france"),
                new GeoPoint(48.8566, 2.3522), 30.0, 45.0, Instant.now()
        );

        StepVerifier.create(repository.save(telemetry))
                .verifyComplete();

        WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .responseTimeout(Duration.ofSeconds(5))
                .build()
                .get()
                .uri("/api/vehicles/hist-v001/history?limit=10")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(1);
    }
}
