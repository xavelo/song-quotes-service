package com.xavelo.common.metrics.autoconfigure;

import com.xavelo.common.metrics.AdapterMetricsAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class AdapterMetricsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AdapterMetricsAspect adapterMetricsAspect() {
        return new AdapterMetricsAspect();
    }
}
