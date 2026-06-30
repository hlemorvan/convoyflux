// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.infrastructure.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TelemetryJsonDecoderTest {

    TelemetryJsonDecoder decoder;

    @BeforeEach
    void setUp() {
        var mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        decoder = new TelemetryJsonDecoder(mapper);
    }

    @Test
    void decodes_valid_payload() throws IOException {
        byte[] payload = """
                {"vehicleId":"v-001","region":"ile-de-france",
                 "lat":48.8566,"lng":2.3522,
                 "speed":60.0,"heading":90.0,
                 "ts":"2026-06-30T10:00:00Z"}
                """.getBytes();

        var t = decoder.decode(payload);

        assertEquals("v-001",           t.vehicleId().value());
        assertEquals("ile-de-france",   t.region().name());
        assertEquals(48.8566,           t.position().lat(), 1e-4);
        assertEquals(2.3522,            t.position().lng(), 1e-4);
        assertEquals(60.0,              t.speed());
        assertEquals(90.0,              t.heading());
        assertEquals(Instant.parse("2026-06-30T10:00:00Z"), t.timestamp());
    }

    @Test
    void ignores_unknown_fields() throws IOException {
        byte[] payload = """
                {"vehicleId":"v-002","region":"idf","lat":48.9,"lng":2.4,
                 "speed":0,"heading":0,"ts":"2026-06-30T10:00:00Z",
                 "extraField":"ignored"}
                """.getBytes();

        assertDoesNotThrow(() -> decoder.decode(payload));
    }

    @Test
    void throws_on_invalid_json() {
        assertThrows(IOException.class, () -> decoder.decode("not-json".getBytes()));
    }
}
