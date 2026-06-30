// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.simulator;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mqtt.broker")
public class MqttBrokerProperties {

    private String host = "localhost";
    private int    port = 1883;

    public String getHost() { return host; }
    public void   setHost(String host) { this.host = host; }
    public int    getPort() { return port; }
    public void   setPort(int port) { this.port = port; }
}
