// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.integration;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MqttIngestIT extends AbstractIntegrationTest {

    @Autowired
    DatabaseClient db;

    @Test
    void published_mqtt_message_is_persisted_in_db() throws Exception {
        String payload = """
                {"vehicleId":"it-v001","region":"ile-de-france",
                 "lat":48.8566,"lng":2.3522,"speed":50.0,"heading":90.0,
                 "ts":"%s"}""".formatted(Instant.now());

        try (Mqtt5BlockingClient client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(HIVEMQ.getHost())
                .serverPort(HIVEMQ.getMappedPort(1883))
                .buildBlocking()) {

            client.connect();
            client.publishWith()
                    .topic("fleet/ile-de-france/vehicle/it-v001/telemetry")
                    .qos(MqttQos.AT_MOST_ONCE)
                    .payload(payload.getBytes())
                    .send();
            client.disconnect();
        }

        // Laisse le temps à l'adapter de traiter le message
        Thread.sleep(500);

        StepVerifier.create(
                db.sql("SELECT count(*) FROM telemetry WHERE vehicle_id = 'it-v001'")
                  .map(row -> row.get(0, Long.class))
                  .one()
        )
        .expectNextMatches(count -> count >= 1)
        .verifyComplete();
    }
}
