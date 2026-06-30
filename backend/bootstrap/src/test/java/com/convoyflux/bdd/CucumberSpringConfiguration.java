// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.bdd;

import com.convoyflux.ConvoyFluxApplication;
import com.convoyflux.integration.AbstractIntegrationTest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = ConvoyFluxApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration extends AbstractIntegrationTest {}
