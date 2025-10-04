package com.xavelo.common.metrics;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdapterMetricsAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    private AdapterMetricsAspect aspect;

    private SimpleMeterRegistry meterRegistry;

    private CountAdapterInvocation annotation;

    @BeforeEach
    void setUp() {
        aspect = new AdapterMetricsAspect();
        meterRegistry = new SimpleMeterRegistry();
        Metrics.globalRegistry.add(meterRegistry);
        annotation = new CountAdapterInvocation() {
            @Override
            public String name() {
                return "test-adapter";
            }

            @Override
            public AdapterMetrics.Direction direction() {
                return AdapterMetrics.Direction.OUT;
            }

            @Override
            public AdapterMetrics.Type type() {
                return AdapterMetrics.Type.HTTP;
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return CountAdapterInvocation.class;
            }
        };
    }

    @AfterEach
    void tearDown() {
        Metrics.globalRegistry.remove(meterRegistry);
        meterRegistry.close();
    }

    @Test
    void countAdapterInvocation_recordsErrorOnThrowable() throws Throwable {
        Error error = new Error("boom");
        when(joinPoint.proceed()).thenThrow(error);

        assertThatThrownBy(() -> aspect.countAdapterInvocation(joinPoint, annotation))
                .isSameAs(error);

        double errorCount = meterRegistry.get("adapter.invocation")
                .tag("name", "test-adapter")
                .tag("type", "http")
                .tag("direction", "out")
                .tag("result", "error")
                .counter()
                .count();

        assertThat(errorCount).isEqualTo(1.0d);
    }
}
