package com.iamnana.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Failed booking events are retried with exponential backoff and then
 * published to the "booking.DLT" topic instead of blocking the partition.
 * Client errors from the inventory service (e.g. 404/409) will never succeed
 * on retry, so they go straight to the dead-letter topic.
 */
@Configuration
public class KafkaErrorHandlingConfig {

    @Bean
    public CommonErrorHandler kafkaErrorHandler(KafkaOperations<Object, Object> kafkaOperations) {
        ExponentialBackOff backOff = new ExponentialBackOff(1_000L, 2.0);
        backOff.setMaxAttempts(3);

        DefaultErrorHandler handler = new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaOperations), backOff);
        handler.addNotRetryableExceptions(HttpClientErrorException.class);
        return handler;
    }
}
