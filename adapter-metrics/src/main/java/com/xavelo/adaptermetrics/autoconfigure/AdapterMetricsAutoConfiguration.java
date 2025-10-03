package com.xavelo.adaptermetrics.autoconfigure;

import com.xavelo.adaptermetrics.AdapterMetricsAspect;
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
