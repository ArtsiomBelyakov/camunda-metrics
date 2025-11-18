package org.camunda.bpm.run.plugin.monitoring.slo;

import corp.platform.gitlab.observabilitybase.configuration.MetricsProperties;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(name = "slo-metrics.enabled", havingValue = "true")
@Slf4j
public class JaxRsFilterConfig {

  @Bean
  public SloJaxRsRequestFilter sloJaxRsRequestFilter() {
    return new SloJaxRsRequestFilter();
  }

  @Bean
  public SloJaxRsResponseFilter sloJaxRsResponseFilter(
      MeterRegistry meterRegistry,
      MetricsProperties metricsProperties
  ) {
    return new SloJaxRsResponseFilter(meterRegistry, metricsProperties);
  }

  @Bean
  public ResourceConfigCustomizer sloJaxRsConfigCustomizer(
      SloJaxRsRequestFilter sloJaxRsRequestFilter,
      SloJaxRsResponseFilter sloJaxRsResponseFilter
  ) {
    return config -> {
      log.info("Register JaxRs SLO filters");
      config.register(sloJaxRsRequestFilter);
      config.register(sloJaxRsResponseFilter);
    };
  }
}
