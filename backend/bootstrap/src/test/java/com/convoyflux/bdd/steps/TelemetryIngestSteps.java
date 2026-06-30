// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.bdd.steps;

import com.convoyflux.domain.model.GeoPoint;
import com.convoyflux.domain.model.Region;
import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.model.VehicleId;
import com.convoyflux.domain.port.in.IngestTelemetryUseCase;
import com.convoyflux.domain.port.out.TelemetryBroadcast;
import com.convoyflux.domain.port.out.TelemetryRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class TelemetryIngestSteps {

    @Autowired IngestTelemetryUseCase ingestUseCase;
    @Autowired TelemetryRepository    repository;
    @Autowired TelemetryBroadcast     broadcast;

    private Telemetry sentTelemetry;
    private final AtomicReference<Telemetry> receivedFromBroadcast = new AtomicReference<>();

    @Given("un véhicule {string} prêt à publier une position")
    public void un_vehicule_pret(String vehicleId) {
        sentTelemetry = new Telemetry(
                new VehicleId(vehicleId),
                new Region("ile-de-france"),
                new GeoPoint(48.8566, 2.3522),
                55.0, 90.0,
                Instant.now()
        );
    }

    @When("le backend reçoit la position")
    public void le_backend_recoit_la_position() {
        broadcast.asFlux()
                 .take(1)
                 .subscribe(receivedFromBroadcast::set);

        StepVerifier.create(ingestUseCase.ingest(sentTelemetry))
                .verifyComplete();
    }

    @Then("la position est persistée en base")
    public void la_position_est_persistee() {
        StepVerifier.create(
                repository.findByVehicleId(sentTelemetry.vehicleId(), 1)
        )
        .assertNext(t -> assertEquals(sentTelemetry.vehicleId(), t.vehicleId()))
        .verifyComplete();
    }

    @Then("la position est diffusée aux abonnés")
    public void la_position_est_diffusee() throws InterruptedException {
        Thread.sleep(200);
        assertNotNull(receivedFromBroadcast.get());
        assertEquals(sentTelemetry.vehicleId(), receivedFromBroadcast.get().vehicleId());
    }
}
