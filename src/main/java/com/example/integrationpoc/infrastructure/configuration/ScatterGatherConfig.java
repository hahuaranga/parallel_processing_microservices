package com.example.integrationpoc.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import com.example.integrationpoc.domain.model.ConsolidatedResponse;
import com.example.integrationpoc.domain.model.GalacticSystemRequest;
import com.example.integrationpoc.domain.model.PlanetResponse;
import java.util.List;
import org.springframework.integration.store.MessageGroup;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 01-09-2025 at 19:05:26
 * File: ScatterGatherConfig.java
 */

@Configuration
public class ScatterGatherConfig {

    @Bean
    IntegrationFlow scatterGatherFlow() {
        return IntegrationFlow.from("scatter.request.channel")
                .scatterGather(
                        scatterer -> scatterer
                                .applySequence(true)
                                .recipientFlow(flow -> flow
                                        .enrichHeaders(h -> h
                                                .headerExpression("originalRequest", "payload")
                                            )                                		
                                		.transform("payload.codigosPlanetarios") // SpEL
                                		.split()
                                        .handle("planetGateway", "getPlanetInfo")),
                        gatherer -> gatherer
                                .releaseStrategy(releaseStrategy2())
                                .outputProcessor(this::processGatheredResults)
                                .groupTimeout(8000L),
                        spec -> spec.errorChannel("scatterGatherErrorChannel")
                )
                .channel("scatter.response.channel")
                .get();
    }

    @Bean
    ReleaseStrategy releaseStrategy2() {
        return group -> group.size() == group.getOne().getHeaders().get("sequenceSize", Integer.class);
    }
    
    private Object processGatheredResults(MessageGroup group) {
        List<PlanetResponse> planetResponses = group.getMessages().stream()
                .map(Message::getPayload)
                .filter(PlanetResponse.class::isInstance)
                .map(PlanetResponse.class::cast)
                .toList();
        
        GalacticSystemRequest originalRequest = (GalacticSystemRequest) 
                group.getOne().getHeaders().get("originalRequest");
        
        return new ConsolidatedResponse(
                originalRequest.nombre(),
                planetResponses.stream()
                        .map(response -> response.result().properties().name())
                        .toList(),
                planetResponses.size(),
                java.time.Instant.now().toString()
        );
    }
  
}
