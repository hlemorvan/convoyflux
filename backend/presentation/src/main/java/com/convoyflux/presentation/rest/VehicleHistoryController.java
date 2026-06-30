// SPDX-License-Identifier: GPL-3.0-or-later
package com.convoyflux.presentation.rest;

import com.convoyflux.domain.model.VehicleId;
import com.convoyflux.domain.port.in.QueryHistoryUseCase;
import com.convoyflux.presentation.dto.TelemetryDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleHistoryController {

    private final QueryHistoryUseCase queryHistoryUseCase;

    public VehicleHistoryController(QueryHistoryUseCase queryHistoryUseCase) {
        this.queryHistoryUseCase = queryHistoryUseCase;
    }

    @GetMapping(value = "/{vehicleId}/history",
                produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<TelemetryDto> history(
            @PathVariable String vehicleId,
            @RequestParam(defaultValue = "100") int limit) {
        return queryHistoryUseCase.history(new VehicleId(vehicleId), limit)
                .map(TelemetryDto::from);
    }
}
