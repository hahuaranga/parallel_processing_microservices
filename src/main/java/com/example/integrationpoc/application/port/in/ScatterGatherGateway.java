package com.example.integrationpoc.application.port.in;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import com.example.integrationpoc.domain.model.ConsolidatedResponse;
import com.example.integrationpoc.domain.model.GalacticSystemRequest;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 01-09-2025 at 18:47:29
 * File: ScatterGatherGateway.java
 */

@MessagingGateway
public interface ScatterGatherGateway {
    
    @Gateway(requestChannel = "scatter.request.channel", 
             replyChannel = "scatter.response.channel",
             replyTimeout = 10000)
    ConsolidatedResponse executeScatterGather(GalacticSystemRequest request);
}
