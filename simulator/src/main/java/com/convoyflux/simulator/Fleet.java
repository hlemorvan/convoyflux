// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class Fleet implements SmartInitializingSingleton {

    private static final Logger log = LoggerFactory.getLogger(Fleet.class);

    private final List<VehiclePublisher> publishers;
    private final Mqtt5AsyncClient       mqttClient;
    private Disposable                   subscription;

    public Fleet(SimulatorProperties props, MqttBrokerProperties brokerProps) {
        this.mqttClient = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(brokerProps.getHost())
                .serverPort(brokerProps.getPort())
                .buildAsync();

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        this.publishers = IntStream.range(0, props.getVehicleCount())
                .mapToObj(i -> new VehiclePublisher(
                        "v-%03d".formatted(i),
                        props.getRegion(),
                        mqttClient,
                        mapper))
                .toList();
    }

    @Override
    public void afterSingletonsInstantiated() {
        mqttClient.connect()
                  .thenAccept(__ -> {
                      log.info("Simulator connected to MQTT broker, starting {} vehicles",
                               publishers.size());
                      startSimulation();
                  })
                  .exceptionally(ex -> {
                      log.error("Simulator MQTT connection failed", ex);
                      return null;
                  });
    }

    private void startSimulation() {
        // Chaque véhicule publie toutes les secondes avec un décalage initial
        // pour étaler la charge (éviter 50 publishes simultanées).
        subscription = Flux.interval(Duration.ofMillis(20)) // un tick toutes les 20 ms
                .subscribe(tick -> {
                    int idx = (int)(tick % publishers.size());
                    publishers.get(idx).tick();
                    // 50 véhicules × 20ms/tick = chaque véhicule publie toutes les ~1 s
                });
    }
}
