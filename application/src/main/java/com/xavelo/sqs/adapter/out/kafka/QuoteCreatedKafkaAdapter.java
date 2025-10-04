package com.xavelo.sqs.adapter.out.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavelo.common.metrics.CountAdapterInvocation;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.out.PublishQuoteCreatedPort;
import org.springframework.kafka.core.KafkaTemplate;
import com.xavelo.common.metrics.Adapter;

import static com.xavelo.common.metrics.AdapterMetrics.Direction.OUT;
import static com.xavelo.common.metrics.AdapterMetrics.Type.KAFKA;

@Adapter
public class QuoteCreatedKafkaAdapter implements PublishQuoteCreatedPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String SONG_QUOTE_CREATED_TOPIC = "song-quote-created-topic";

    public QuoteCreatedKafkaAdapter(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @CountAdapterInvocation(name = "publish-quote-created", direction = OUT, type = KAFKA)
    public void publishQuoteCreated(Quote quote) {
        try {
            String payload = objectMapper.writeValueAsString(quote);
            kafkaTemplate.send(SONG_QUOTE_CREATED_TOPIC, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize quote", e);
        }
    }
}
