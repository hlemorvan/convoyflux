// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class GeoPointTest {

    @Test
    void valid_paris_coordinates_accepted() {
        var point = new GeoPoint(48.8566, 2.3522);
        assertEquals(48.8566, point.lat());
        assertEquals(2.3522, point.lng());
    }

    @ParameterizedTest
    @CsvSource({"-91, 0", "91, 0", "0, -181", "0, 181"})
    void out_of_range_coordinates_throw(double lat, double lng) {
        assertThrows(IllegalArgumentException.class, () -> new GeoPoint(lat, lng));
    }

    @Test
    void boundary_values_are_accepted() {
        assertDoesNotThrow(() -> new GeoPoint(-90, -180));
        assertDoesNotThrow(() -> new GeoPoint(90, 180));
    }
}
