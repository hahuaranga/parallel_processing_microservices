package com.example.integrationpoc.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aggregator.HeaderAttributeCorrelationStrategy;
import org.springframework.integration.aggregator.MessageGroupProcessor;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.splitter.MethodInvokingSplitter;
import org.springframework.integration.store.SimpleMessageStore;
import org.springframework.messaging.Message;
import com.example.integrationpoc.domain.model.ConsolidatedResponse;
import com.example.integrationpoc.domain.model.GalacticSystemRequest;
import com.example.integrationpoc.domain.model.PlanetResponse;
import com.example.integrationpoc.infrastructure.integration.splitter.GalacticSystemSplitter;
import java.util.List;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 01-09-2025 at 18:57:13
 * File: SplitterAggregateConfig.java
 */

@Configuration
public class SplitterAggregateConfig {

    @Bean
    IntegrationFlow splitterAggregateFlow() {
        return IntegrationFlow.from("splitter.input.channel")
                .split(splitter(), null)
                .handle("planetGateway", "getPlanetInfo")
                .aggregate(aggregatorSpec -> aggregatorSpec
                        .correlationStrategy(new HeaderAttributeCorrelationStrategy("correlationId"))
                        .releaseStrategy(releaseStrategy())
                        .outputProcessor(messageGroupProcessor())
                        .messageStore(new SimpleMessageStore())
                        .groupTimeout(8000L)
                        .sendPartialResultOnExpiry(true)
                        )
                .channel("aggregate.output.channel")
                .get();
    }

    @Bean
    MethodInvokingSplitter splitter() {
        GalacticSystemSplitter splitter = new GalacticSystemSplitter();
        return new MethodInvokingSplitter(splitter, "splitMessage");
    }

    @Bean
    ReleaseStrategy releaseStrategy() {
        return group -> group.size() == group.getOne().getHeaders().get("sequenceSize", Integer.class);
    }

    @Bean
    MessageGroupProcessor messageGroupProcessor() {
        return group -> {
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
        };
    }
    
}
