// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.application;

import com.convoyflux.domain.model.GeoPoint;
import com.convoyflux.domain.model.Region;
import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.model.VehicleId;
import com.convoyflux.domain.port.out.TelemetryBroadcast;
import com.convoyflux.domain.port.out.TelemetryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelemetryServiceTest {

    @Mock TelemetryRepository repository;
    @Mock TelemetryBroadcast  broadcast;

    TelemetryService service;

    private static final Telemetry TELEMETRY = new Telemetry(
            new VehicleId("v-001"),
            new Region("ile-de-france"),
            new GeoPoint(48.8566, 2.3522),
            60.0, 90.0,
            Instant.parse("2026-06-30T10:00:00Z")
    );

    @BeforeEach
    void setUp() {
        service = new TelemetryService(repository, broadcast);
    }

    @Test
    void ingest_saves_then_broadcasts() {
        when(repository.save(TELEMETRY)).thenReturn(Mono.empty());

        StepVerifier.create(service.ingest(TELEMETRY))
                .verifyComplete();

        verify(repository).save(TELEMETRY);
        verify(broadcast).publish(TELEMETRY);
    }

    @Test
    void ingest_does_not_broadcast_on_save_error() {
        when(repository.save(any())).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(service.ingest(TELEMETRY))
                .verifyError(RuntimeException.class);

        verify(broadcast, never()).publish(any());
    }

    @Test
    void history_delegates_to_repository() {
        var vehicleId = new VehicleId("v-001");
        when(repository.findByVehicleId(vehicleId, 50))
                .thenReturn(Flux.just(TELEMETRY));

        StepVerifier.create(service.history(vehicleId, 50))
                .expectNext(TELEMETRY)
                .verifyComplete();
    }
}
