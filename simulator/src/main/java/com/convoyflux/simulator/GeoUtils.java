// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.simulator;

/**
 * Utilitaires géographiques pour la simulation en Île-de-France.
 * Bbox : lat [48.5, 49.1], lng [1.8, 2.8]
 */
public final class GeoUtils {

    public static final double LAT_MIN = 48.5;
    public static final double LAT_MAX = 49.1;
    public static final double LNG_MIN =  1.8;
    public static final double LNG_MAX =  2.8;

    private GeoUtils() {}

    /** Borne lat dans le bbox Île-de-France. */
    public static double clampLat(double lat) {
        return Math.max(LAT_MIN, Math.min(LAT_MAX, lat));
    }

    /** Borne lng dans le bbox Île-de-France. */
    public static double clampLng(double lng) {
        return Math.max(LNG_MIN, Math.min(LNG_MAX, lng));
    }

    /**
     * Calcule le cap (heading) en degrés [0, 360) entre deux points.
     * 0° = Nord, 90° = Est.
     */
    public static double heading(double fromLat, double fromLng, double toLat, double toLng) {
        double dLng = Math.toRadians(toLng - fromLng);
        double lat1 = Math.toRadians(fromLat);
        double lat2 = Math.toRadians(toLat);

        double x = Math.sin(dLng) * Math.cos(lat2);
        double y = Math.cos(lat1) * Math.sin(lat2)
                 - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLng);

        double bearing = Math.toDegrees(Math.atan2(x, y));
        return (bearing + 360) % 360;
    }
}
