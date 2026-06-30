// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.simulator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class GeoUtilsTest {

    @Test
    void clampLat_keeps_value_within_bounds() {
        assertEquals(GeoUtils.LAT_MIN, GeoUtils.clampLat(GeoUtils.LAT_MIN - 1));
        assertEquals(GeoUtils.LAT_MAX, GeoUtils.clampLat(GeoUtils.LAT_MAX + 1));
        assertEquals(48.85, GeoUtils.clampLat(48.85), 1e-6);
    }

    @Test
    void clampLng_keeps_value_within_bounds() {
        assertEquals(GeoUtils.LNG_MIN, GeoUtils.clampLng(GeoUtils.LNG_MIN - 1));
        assertEquals(GeoUtils.LNG_MAX, GeoUtils.clampLng(GeoUtils.LNG_MAX + 1));
        assertEquals(2.35, GeoUtils.clampLng(2.35), 1e-6);
    }

    @ParameterizedTest
    @CsvSource({
        // from, to, expected heading (approx)
        "48.8, 2.3, 48.9, 2.3, 0.0",    // Nord
        "48.9, 2.3, 48.8, 2.3, 180.0",  // Sud
        "48.8, 2.3, 48.8, 2.4, 90.0",   // Est
        "48.8, 2.4, 48.8, 2.3, 270.0",  // Ouest
    })
    void heading_returns_correct_cardinal(double fromLat, double fromLng,
                                          double toLat, double toLng,
                                          double expected) {
        double h = GeoUtils.heading(fromLat, fromLng, toLat, toLng);
        assertEquals(expected, h, 1.0); // tolérance 1°
    }

    @Test
    void heading_is_always_in_0_360() {
        for (int i = 0; i < 100; i++) {
            double h = GeoUtils.heading(
                    48.5 + Math.random() * 0.6,
                    1.8  + Math.random() * 1.0,
                    48.5 + Math.random() * 0.6,
                    1.8  + Math.random() * 1.0
            );
            assertTrue(h >= 0 && h < 360, "heading hors [0,360): " + h);
        }
    }
}
