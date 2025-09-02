package com.example.integrationpoc.infrastructure.integration.splitter;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import com.example.integrationpoc.domain.model.GalacticSystemRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 01-09-2025 at 19:02:19
 * File: GalacticSystemSplitter.java
 */

public class GalacticSystemSplitter {
    
    public List<Message<String>> splitMessage(Message<?> message) {
        GalacticSystemRequest request = (GalacticSystemRequest) message.getPayload();
        List<String> planetCodes = request.codigosPlanetarios();
        
        return planetCodes.stream()
                .map(planetCode -> {
                    return MessageBuilder.withPayload(planetCode)
                            .setHeader("correlationId", message.getHeaders().getId())
                            .setHeader("sequenceSize", planetCodes.size())
                            .setHeader("sequenceNumber", planetCodes.indexOf(planetCode) + 1)
                            .setHeader("originalRequest", request)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
