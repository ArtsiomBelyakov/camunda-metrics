package org.camunda.bpm.run.plugin.monitoring;

import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterFilter excludeAllExceptEngineRest() {
        return MeterFilter.deny(id -> {
            // Исключаем все метрики, кроме тех, которые начинаются с "http.server.requests" и "/engine-rest/"
            if (id.getName().startsWith("http.server.requests")) {
                return id.getTags().stream()
                        .noneMatch(tag -> tag.getKey().equals("uri") && tag.getValue().startsWith("/engine-rest/"));
            }
            return false; // Оставляем все остальные метрики
        });
    }
}
