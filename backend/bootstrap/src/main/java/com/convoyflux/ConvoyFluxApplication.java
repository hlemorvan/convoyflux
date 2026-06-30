// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.convoyflux")
public class ConvoyFluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConvoyFluxApplication.class, args);
    }
}
