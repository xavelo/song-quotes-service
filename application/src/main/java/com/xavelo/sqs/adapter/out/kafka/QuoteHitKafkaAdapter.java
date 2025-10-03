package com.xavelo.sqs.adapter.out.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavelo.sqs.adapter.CountAdapterInvocation;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.out.PublishQuoteHitPort;
import org.springframework.kafka.core.KafkaTemplate;
import com.xavelo.sqs.adapter.Adapter;

import static com.xavelo.sqs.adapter.AdapterMetrics.Direction.OUT;
import static com.xavelo.sqs.adapter.AdapterMetrics.Type.KAFKA;

@Adapter
public class QuoteHitKafkaAdapter implements PublishQuoteHitPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String SONG_QUOTE_HIT_TOPIC = "song-quote-hit-topic";

    public QuoteHitKafkaAdapter(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @CountAdapterInvocation(name = "publish-quote-hit", direction = OUT, type = KAFKA)
    public void publishQuoteHit(Quote quote) {
        try {
            String payload = objectMapper.writeValueAsString(quote);
            kafkaTemplate.send(SONG_QUOTE_HIT_TOPIC, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize quote", e);
        }
    }
}
