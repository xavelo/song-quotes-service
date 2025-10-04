package com.xavelo.common.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class AdapterMetricsAspectTest {

    private SimpleMeterRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        Metrics.globalRegistry.add(registry);
    }

    @AfterEach
    void tearDown() {
        Metrics.globalRegistry.remove(registry);
        registry.close();
    }

    @Test
    void successfulInvocationIncrementsSuccessCounterAndRecordsDuration() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(TestConfiguration.class);
            context.refresh();

            TestAdapter adapter = context.getBean(TestAdapter.class);
            adapter.invoke();

            var successCounter = registry
                    .find(AdapterMetrics.METRIC_ADAPTER_INVOCATIONS)
                    .tags("adapter", "test-adapter", "type", "HTTP", "direction", "OUT", "result", "SUCCESS")
                    .counter();
            assertThat(successCounter).isNotNull();
            assertThat(successCounter.count()).isEqualTo(1.0d);

            Timer timer = registry
                    .find(AdapterMetrics.METRIC_ADAPTER_DURATION)
                    .tags("adapter", "test-adapter", "type", "HTTP", "direction", "OUT")
                    .timer();
            assertThat(timer).isNotNull();
            assertThat(timer.count()).isEqualTo(1L);
        }
    }

    @Test
    void failedInvocationIncrementsErrorCounterAndPropagatesException() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(TestConfiguration.class);
            context.refresh();

            TestAdapter adapter = context.getBean(TestAdapter.class);

            assertThatThrownBy(adapter::fail)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("boom");

            var errorCounter = registry
                    .find(AdapterMetrics.METRIC_ADAPTER_INVOCATIONS)
                    .tags("adapter", "test-adapter", "type", "HTTP", "direction", "OUT", "result", "ERROR")
                    .counter();
            assertThat(errorCounter).isNotNull();
            assertThat(errorCounter.count()).isEqualTo(1.0d);
        }
    }

    @Configuration
    @EnableAdapterMetrics
    static class TestConfiguration {

        @Bean
        TestAdapter testAdapter() {
            return new TestAdapter();
        }
    }

    static class TestAdapter {

        @CountAdapterInvocation(name = "test-adapter", type = AdapterMetrics.Type.HTTP, direction = AdapterMetrics.Direction.OUT)
        public String invoke() {
            return "ok";
        }

        @CountAdapterInvocation(name = "test-adapter", type = AdapterMetrics.Type.HTTP, direction = AdapterMetrics.Direction.OUT)
        public String fail() {
            throw new IllegalStateException("boom");
        }
    }
}
