package com.example.integrationpoc.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.handler.advice.ErrorMessageSendingRecoverer;
import org.springframework.messaging.MessageHandler;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 01-09-2025 at 19:14:57
 * File: ErrorHandlingConfig.java
 */

@Configuration
public class ErrorHandlingConfig {

    @Bean
    @ServiceActivator(inputChannel = "scatterGatherErrorChannel")
    MessageHandler errorHandler() {
        return message -> {
            System.err.println("Error en procesamiento: " + message.getPayload());
            // Aquí puedes agregar lógica adicional de manejo de errores
        };
    }

    @Bean
    ErrorMessageSendingRecoverer errorMessageSendingRecoverer() {
        return new ErrorMessageSendingRecoverer();
    }
}
