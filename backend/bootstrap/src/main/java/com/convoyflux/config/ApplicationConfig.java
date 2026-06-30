// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.config;

import com.convoyflux.application.TelemetryService;
import com.convoyflux.domain.port.out.TelemetryBroadcast;
import com.convoyflux.domain.port.out.TelemetryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public TelemetryService telemetryService(TelemetryRepository repository,
                                              TelemetryBroadcast broadcast) {
        return new TelemetryService(repository, broadcast);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
