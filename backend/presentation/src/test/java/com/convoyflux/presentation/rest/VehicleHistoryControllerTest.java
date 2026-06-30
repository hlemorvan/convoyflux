// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.presentation.rest;

import com.convoyflux.domain.model.GeoPoint;
import com.convoyflux.domain.model.Region;
import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.model.VehicleId;
import com.convoyflux.domain.port.in.QueryHistoryUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleHistoryControllerTest {

    @Mock QueryHistoryUseCase queryHistoryUseCase;

    @Test
    void history_returns_mapped_dtos() {
        var t = new Telemetry(
                new VehicleId("v-001"), new Region("idf"),
                new GeoPoint(48.8, 2.3), 50.0, 180.0, Instant.now()
        );
        when(queryHistoryUseCase.history(any(), eq(100))).thenReturn(Flux.just(t));

        var controller = new VehicleHistoryController(queryHistoryUseCase);

        StepVerifier.create(controller.history("v-001", 100))
                .assertNext(dto -> {
                    assert dto.vehicleId().equals("v-001");
                    assert dto.speed() == 50.0;
                })
                .verifyComplete();
    }
}
