package com.xavelo.sqs.adapter.out.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.out.PublishQuoteCreatedPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class QuoteCreatedKafkaAdapter implements PublishQuoteCreatedPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public QuoteCreatedKafkaAdapter(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishQuoteCreated(Quote quote) {
        try {
            String payload = objectMapper.writeValueAsString(quote);
            kafkaTemplate.send("song-quote-created", payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize quote", e);
        }
    }
}
