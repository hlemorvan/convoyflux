// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleIdTest {

    @Test
    void valid_value_is_accepted() {
        var id = new VehicleId("v-001");
        assertEquals("v-001", id.value());
    }

    @Test
    void null_value_throws() {
        assertThrows(IllegalArgumentException.class, () -> new VehicleId(null));
    }

    @Test
    void blank_value_throws() {
        assertThrows(IllegalArgumentException.class, () -> new VehicleId("  "));
    }

    @Test
    void equality_is_value_based() {
        assertEquals(new VehicleId("v-001"), new VehicleId("v-001"));
        assertNotEquals(new VehicleId("v-001"), new VehicleId("v-002"));
    }
}
