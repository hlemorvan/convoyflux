// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.Random;

/**
 * Simule un véhicule unique par marche aléatoire dans le bbox Île-de-France.
 * Publie sa position en JSON sur fleet/{region}/vehicle/{id}/telemetry, QoS 0.
 *
 * Note prod : Protobuf ou CBOR réduirait la taille du payload et la charge CPU
 * de sérialisation sur un device embarqué contraint.
 */
public class VehiclePublisher {

    private static final Logger log = LoggerFactory.getLogger(VehiclePublisher.class);

    // Pas de déplacement par tick (~1s) en degrés ≈ ~110m en lat, ~80m en lng
    private static final double STEP_LAT = 0.001;
    private static final double STEP_LNG = 0.001;

    private final String           vehicleId;
    private final String           region;
    private final Mqtt5AsyncClient mqttClient;
    private final ObjectMapper     objectMapper;
    private final Random           random = new Random();

    private double prevLat;
    private double prevLng;
    private double lat;
    private double lng;

    VehiclePublisher(String vehicleId, String region,
                     Mqtt5AsyncClient mqttClient, ObjectMapper objectMapper) {
        this.vehicleId    = vehicleId;
        this.region       = region;
        this.mqttClient   = mqttClient;
        this.objectMapper = objectMapper;

        // Position initiale aléatoire dans le bbox
        this.lat     = GeoUtils.LAT_MIN + random.nextDouble() * (GeoUtils.LAT_MAX - GeoUtils.LAT_MIN);
        this.lng     = GeoUtils.LNG_MIN + random.nextDouble() * (GeoUtils.LNG_MAX - GeoUtils.LNG_MIN);
        this.prevLat = lat;
        this.prevLng = lng;
    }

    void tick() {
        prevLat = lat;
        prevLng = lng;

        lat = GeoUtils.clampLat(lat + (random.nextDouble() - 0.5) * 2 * STEP_LAT);
        lng = GeoUtils.clampLng(lng + (random.nextDouble() - 0.5) * 2 * STEP_LNG);

        double heading = GeoUtils.heading(prevLat, prevLng, lat, lng);
        double speed   = 10 + random.nextDouble() * 120; // 10-130 km/h

        String topic = "fleet/%s/vehicle/%s/telemetry".formatted(region, vehicleId);

        try {
            byte[] payload = objectMapper.writeValueAsBytes(Map.of(
                    "vehicleId", vehicleId,
                    "region",    region,
                    "lat",       lat,
                    "lng",       lng,
                    "speed",     speed,
                    "heading",   heading,
                    "ts",        Instant.now().toString()
            ));

            mqttClient.publishWith()
                      .topic(topic)
                      .qos(MqttQos.AT_MOST_ONCE)
                      .payload(payload)
                      .send();
        } catch (Exception e) {
            log.warn("Failed to publish telemetry for {}", vehicleId, e);
        }
    }
}
