package com.example.integrationpoc.application.port.out;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import com.example.integrationpoc.domain.model.PlanetResponse;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 02-09-2025 at 17:28:39
 * File: PlanetGateway.java
 */

@MessagingGateway
public interface PlanetGateway {
    
    @Gateway(requestChannel = "planet.request.channel", 
             replyChannel = "planet.response.channel",
             replyTimeout = 5000)
    PlanetResponse getPlanetInfo(String planetCode); // Síncrono
}
