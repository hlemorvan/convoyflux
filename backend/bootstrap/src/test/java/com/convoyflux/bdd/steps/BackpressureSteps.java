// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.bdd.steps;

import com.convoyflux.domain.model.GeoPoint;
import com.convoyflux.domain.model.Region;
import com.convoyflux.domain.model.Telemetry;
import com.convoyflux.domain.model.VehicleId;
import com.convoyflux.domain.port.out.TelemetryBroadcast;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BackpressureSteps {

    @Autowired TelemetryBroadcast broadcast;

    private Telemetry first;
    private Telemetry second;
    private final List<Telemetry> received = new ArrayList<>();

    @Given("deux positions coup sur coup pour le même véhicule")
    public void deux_positions_coup_sur_coup() {
        var vehicleId = new VehicleId("bp-v001");
        var region    = new Region("ile-de-france");
        var now       = Instant.now();

        first  = new Telemetry(vehicleId, region, new GeoPoint(48.85, 2.35),  10.0, 0.0, now);
        second = new Telemetry(vehicleId, region, new GeoPoint(48.852, 2.352), 15.0, 5.0, now.plusSeconds(1));
    }

    @When("les deux positions sont publiées dans le broadcast")
    public void les_deux_positions_sont_publiees() {
        broadcast.asFlux()
                 .take(2)
                 .subscribe(received::add);

        broadcast.publish(first);
        broadcast.publish(second);
    }

    @Then("au moins la position la plus récente est diffusée")
    public void au_moins_la_plus_recente_est_diffusee() throws InterruptedException {
        Thread.sleep(200);
        // directBestEffort : les deux peuvent passer si le subscriber est rapide.
        // L'invariant minimal : la position la plus récente (second) est présente.
        assertTrue(
            received.contains(second),
            "La position la plus récente doit être dans les messages reçus"
        );
    }
}
