package com.example.integrationpoc.application.port.in;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.integrationpoc.domain.model.ConsolidatedResponse;
import com.example.integrationpoc.domain.model.GalacticSystemRequest;

import lombok.RequiredArgsConstructor;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 01-09-2025 at 19:11:06
 * File: GalaxyController.java
 */

@RestController
@RequestMapping("/api/galaxy")
@RequiredArgsConstructor
public class GalaxyController {

    private final SplitterAggregateGateway splitterAggregateGateway;
    
    private final ScatterGatherGateway scatterGatherGateway;

    @PostMapping("/splitter-aggregate")
    public ResponseEntity<ConsolidatedResponse> executeSplitterAggregate(
            @RequestBody GalacticSystemRequest request) {
        try {
            ConsolidatedResponse response = splitterAggregateGateway.executeSplitterAggregate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ConsolidatedResponse(
                            request.nombre(),
                            List.of("Error: " + e.getMessage()),
                            0,
                            java.time.Instant.now().toString()
                    ));
        }
    }

    @PostMapping("/scatter-gather")
    public ResponseEntity<ConsolidatedResponse> executeScatterGather(
            @RequestBody GalacticSystemRequest request) {
        try {
            ConsolidatedResponse response = scatterGatherGateway.executeScatterGather(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ConsolidatedResponse(
                            request.nombre(),
                            List.of("Error: " + e.getMessage()),
                            0,
                            java.time.Instant.now().toString()
                    ));
        }
    }
}
