// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.domain.model;

public record VehicleId(String value) {

    public VehicleId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("VehicleId cannot be blank");
        }
    }
}
