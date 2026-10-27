package com.pelada.email_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfig {

    @Bean
    public DefaultErrorHandler errorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate,
            @Value("${email.retentativas:3}") long retentativas,
            @Value("${email.intervalo-retentativa-ms:5000}") long intervaloMs) {
        DeadLetterPublishingRecoverer filaDeErro = new DeadLetterPublishingRecoverer(kafkaTemplate);
        return new DefaultErrorHandler(filaDeErro, new FixedBackOff(intervaloMs, retentativas));
    }
}
