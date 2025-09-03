package com.example.integrationpoc.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.advice.ExpressionEvaluatingRequestHandlerAdvice;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;
import com.example.integrationpoc.domain.model.ConsolidatedResponse;
import com.example.integrationpoc.domain.model.GalacticSystemRequest;
import com.example.integrationpoc.domain.model.PlanetResponse;
import java.util.List;
import java.util.concurrent.Executors;

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
                                        .transform("payload.codigosPlanetarios")
                                        .split()
                                        .channel(c -> c.executor(Executors.newCachedThreadPool())) // <-- PARALELO
                                        .handle("planetGateway", "getPlanetInfo", e -> e.advice(errorHandlingAdviceSG()))
                                        .filter(PlanetResponse.class::isInstance) // <-- best-effort
                                ),
                        gatherer -> gatherer
                                .releaseStrategy(releaseStrategySG())
                                .outputProcessor(this::processGatheredResults)
                                .groupTimeout(1500L)
                                .sendPartialResultOnExpiry(true),
                        spec -> spec.errorChannel("scatterGatherErrorChannel")
                )
                .channel("scatter.response.channel")
                .get();
    }

    @Bean
    ReleaseStrategy releaseStrategySG() {
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

    @Bean
    ExpressionEvaluatingRequestHandlerAdvice errorHandlingAdviceSG() {
        ExpressionEvaluatingRequestHandlerAdvice advice = new ExpressionEvaluatingRequestHandlerAdvice();
        advice.setOnFailureExpressionString("null");
        advice.setReturnFailureExpressionResult(true);
        return advice;
    }
}
