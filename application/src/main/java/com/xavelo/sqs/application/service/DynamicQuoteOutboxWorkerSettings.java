package com.xavelo.sqs.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DynamicQuoteOutboxWorkerSettings {

    private static final String PROPERTY_SOURCE_NAME = "dynamicQuoteOutboxWorkerSettings";
    private static final String BATCH_SIZE_PROPERTY = "quote-events.outbox.worker.batch-size";

    private final AtomicInteger batchSize;
    private final Map<String, Object> propertyValues;
    private final MapPropertySource propertySource;

    public DynamicQuoteOutboxWorkerSettings(
            ConfigurableEnvironment environment,
            @Value("${quote-events.outbox.worker.batch-size:25}") int defaultBatchSize) {
        this.batchSize = new AtomicInteger(defaultBatchSize);
        this.propertyValues = new ConcurrentHashMap<>();
        this.propertyValues.put(BATCH_SIZE_PROPERTY, defaultBatchSize);
        this.propertySource = new MapPropertySource(PROPERTY_SOURCE_NAME, propertyValues);

        MutablePropertySources propertySources = environment.getPropertySources();
        if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
            propertySources.replace(PROPERTY_SOURCE_NAME, propertySource);
        } else {
            propertySources.addFirst(propertySource);
        }
    }

    public int getBatchSize() {
        return batchSize.get();
    }

    public void updateBatchSize(int newBatchSize) {
        if (newBatchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be greater than zero");
        }
        batchSize.set(newBatchSize);
        propertyValues.put(BATCH_SIZE_PROPERTY, newBatchSize);
    }
}
