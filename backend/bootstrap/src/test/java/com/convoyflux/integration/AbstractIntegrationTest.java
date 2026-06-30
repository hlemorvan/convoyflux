// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(
                    DockerImageName.parse("postgis/postgis:16-3.4")
                                   .asCompatibleSubstituteFor("postgres"))
                    .withDatabaseName("convoyflux")
                    .withUsername("convoyflux")
                    .withPassword("convoyflux");

    @Container
    static final GenericContainer<?> HIVEMQ =
            new GenericContainer<>("hivemq/hivemq-ce:latest")
                    .withExposedPorts(1883)
                    .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                "r2dbc:postgresql://%s:%d/convoyflux"
                        .formatted(POSTGRES.getHost(), POSTGRES.getMappedPort(5432)));
        registry.add("spring.r2dbc.username", POSTGRES::getUsername);
        registry.add("spring.r2dbc.password", POSTGRES::getPassword);

        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);

        registry.add("mqtt.broker.host", HIVEMQ::getHost);
        registry.add("mqtt.broker.port", () -> HIVEMQ.getMappedPort(1883));
    }
}
