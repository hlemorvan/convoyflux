// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.domain.model;

public record Region(String name) {

    public Region {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Region name cannot be blank");
        }
    }
}
