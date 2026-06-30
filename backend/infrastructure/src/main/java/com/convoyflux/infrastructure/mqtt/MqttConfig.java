// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.infrastructure.mqtt;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MqttProperties.class)
public class MqttConfig {

    @Bean
    public Mqtt5AsyncClient mqtt5AsyncClient(MqttProperties props) {
        return MqttClient.builder()
                .useMqttVersion5()
                .serverHost(props.getHost())
                .serverPort(props.getPort())
                .buildAsync();
    }
}
