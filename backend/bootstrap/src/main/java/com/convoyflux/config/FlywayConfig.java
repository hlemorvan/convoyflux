// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure Flyway explicitement car l'application n'expose pas de DataSource JDBC
 * (seul R2DBC est utilisé en production). L'URL JDBC n'est utilisée qu'à l'init,
 * pour appliquer les migrations, puis ignorée pour le reste du cycle de vie.
 */
@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate")
    public Flyway flyway(
            @Value("${spring.flyway.url}")      String url,
            @Value("${spring.flyway.user:convoyflux}") String user,
            @Value("${spring.flyway.password:convoyflux}") String password,
            @Value("${spring.flyway.locations:classpath:db/migration}") String locations
    ) {
        return Flyway.configure()
                .dataSource(url, user, password)
                .locations(locations)
                .load();
    }
}
