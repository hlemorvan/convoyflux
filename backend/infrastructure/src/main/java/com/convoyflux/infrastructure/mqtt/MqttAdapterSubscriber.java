// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.infrastructure.mqtt;

import com.convoyflux.domain.port.in.IngestTelemetryUseCase;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 * Pont MQTT → Reactor.
 *
 * MQTT est push/callback ; Reactor est pull. Ce composant connecte le broker,
 * s'abonne au wildcard fleet/+/vehicle/+/telemetry (QoS 0 — at-most-once,
 * adapté à la télémétrie haute-fréquence) et pousse chaque message dans
 * IngestTelemetryUseCase qui alimente le Sinks.
 *
 * La backpressure device→broker n'est pas propagée par MQTT : l'impédance
 * est absorbée par le Sinks.Many.directBestEffort() — les positions périmées
 * sont silencieusement droppées pour les abonnés lents.
 */
@Component
public class MqttAdapterSubscriber implements SmartInitializingSingleton {

    private static final Logger log = LoggerFactory.getLogger(MqttAdapterSubscriber.class);
    private static final String TOPIC_FILTER = "fleet/+/vehicle/+/telemetry";

    private final Mqtt5AsyncClient      client;
    private final TelemetryJsonDecoder  decoder;
    private final IngestTelemetryUseCase ingestUseCase;

    public MqttAdapterSubscriber(Mqtt5AsyncClient client,
                                  TelemetryJsonDecoder decoder,
                                  IngestTelemetryUseCase ingestUseCase) {
        this.client       = client;
        this.decoder      = decoder;
        this.ingestUseCase = ingestUseCase;
    }

    @Override
    public void afterSingletonsInstantiated() {
        client.connect()
              .thenAccept(__ -> {
                  log.info("MQTT connected, subscribing to {}", TOPIC_FILTER);
                  subscribeToFleet();
              })
              .exceptionally(ex -> {
                  log.error("MQTT connection failed", ex);
                  return null;
              });
    }

    private void subscribeToFleet() {
        client.subscribeWith()
              .topicFilter(TOPIC_FILTER)
              .qos(MqttQos.AT_MOST_ONCE)
              .callback(publish -> {
                  byte[] payload = publish.getPayload()
                          .map(bb -> {
                              byte[] arr = new byte[bb.remaining()];
                              bb.get(arr);
                              return arr;
                          })
                          .orElse(new byte[0]);
                  try {
                      var telemetry = decoder.decode(payload);
                      ingestUseCase.ingest(telemetry)
                                   .subscribe(
                                       null,
                                       err -> log.warn("Ingest error for {}", publish.getTopic(), err)
                                   );
                  } catch (Exception e) {
                      log.warn("Failed to decode MQTT payload on topic {}", publish.getTopic(), e);
                  }
              })
              .send()
              .whenComplete((subAck, ex) -> {
                  if (ex != null) log.error("MQTT subscribe failed", ex);
                  else            log.info("MQTT subscribe OK: {}", subAck.getReasonCodes());
              });
    }
}
