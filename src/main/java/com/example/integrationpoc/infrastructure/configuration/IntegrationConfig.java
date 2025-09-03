package com.example.integrationpoc.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.http.dsl.Http;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import com.example.integrationpoc.domain.model.PlanetResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 01-09-2025 at 18:51:46
 * File: IntegrationConfig.java
 */

@Configuration
@EnableIntegration
public class IntegrationConfig {
	
    // Flujo para consulta individual de planetas
    @Bean
    IntegrationFlow planetInfoFlow() {
        return IntegrationFlow.from("planet.request.channel")
                .handle(Http.outboundGateway("https://www.swapi.tech/api/planets/{planetCode}")
                        .uriVariable("planetCode", "payload")
                        .httpMethod(HttpMethod.GET)
                        .expectedResponseType(String.class)
                        ,e -> e.advice(retryAdvice())
                        )
                .transform(this::transformToPlanetResponse)
                .channel("planet.response.channel")
                .get();
    }
    
    // Configuración de retry para manejo de errores
    @Bean
    RequestHandlerRetryAdvice retryAdvice() {
        RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();
        
        RetryTemplate retryTemplate = new RetryTemplate();
        
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(10000);
        
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        advice.setRetryTemplate(retryTemplate);
        return advice;
    }
    
    private PlanetResponse transformToPlanetResponse(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, PlanetResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error transformando JSON a PlanetResponse", e);
        }
    }
}
