// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.simulator;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "simulator")
public class SimulatorProperties {

    private int    vehicleCount = 50;
    private String region       = "ile-de-france";

    public int    getVehicleCount() { return vehicleCount; }
    public void   setVehicleCount(int vehicleCount) { this.vehicleCount = vehicleCount; }
    public String getRegion()       { return region; }
    public void   setRegion(String region) { this.region = region; }
}
