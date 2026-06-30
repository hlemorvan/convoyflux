// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.domain.model;

public record GeoPoint(double lat, double lng) {

    public GeoPoint {
        if (lat < -90 || lat > 90) {
            throw new IllegalArgumentException("lat must be in [-90, 90], got: " + lat);
        }
        if (lng < -180 || lng > 180) {
            throw new IllegalArgumentException("lng must be in [-180, 180], got: " + lng);
        }
    }
}
