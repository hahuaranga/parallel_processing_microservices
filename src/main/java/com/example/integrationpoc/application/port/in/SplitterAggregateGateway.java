package com.example.integrationpoc.application.port.in;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import com.example.integrationpoc.domain.model.ConsolidatedResponse;
import com.example.integrationpoc.domain.model.GalacticSystemRequest;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 01-09-2025 at 18:49:28
 * File: SplitterAggregateGateway.java
 */

@MessagingGateway
public interface SplitterAggregateGateway {
    
    @Gateway(requestChannel = "splitter.input.channel", 
             replyChannel = "aggregate.output.channel",
             replyTimeout = 10000)
    ConsolidatedResponse executeSplitterAggregate(GalacticSystemRequest request);
}
