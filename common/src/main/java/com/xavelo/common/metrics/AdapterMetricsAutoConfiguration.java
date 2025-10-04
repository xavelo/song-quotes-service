package com.xavelo.common.metrics;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration that exposes the {@link AdapterMetricsAspect} bean.
 */
@AutoConfiguration
public class AdapterMetricsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AdapterMetricsAspect adapterMetricsAspect() {
        return new AdapterMetricsAspect();
    }
}
