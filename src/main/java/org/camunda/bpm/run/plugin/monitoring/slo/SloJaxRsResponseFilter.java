package org.camunda.bpm.run.plugin.monitoring.slo;

import corp.platform.gitlab.observabilitybase.configuration.MetricsProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.uri.UriTemplate;

@Provider
@RequiredArgsConstructor
@Slf4j
public class SloJaxRsResponseFilter implements ContainerResponseFilter {
  private final MeterRegistry registry;
  private final MetricsProperties metricsProperties;

  @Override
  public void filter(
      ContainerRequestContext requestContext,
      ContainerResponseContext responseContext
  ) {
    Object obj = requestContext.getProperty(SloJaxRsRequestFilter.REQUEST_START_TIME);
    if (obj == null) {
      return;
    }
    long startTime = (long) obj;
    long duration = System.nanoTime() - startTime;
    var matchedTemplates = ((ContainerRequest) requestContext).getUriInfo()
        .getMatchedTemplates();
    var endpointList = matchedTemplates.stream()
        .map(UriTemplate::getTemplate)
        .filter(template -> !template.equals("/"))
        .collect(Collectors.toCollection(ArrayList::new));
    Collections.reverse(endpointList);

    var endpoint = String.join("", endpointList);
    var basePath = requestContext.getUriInfo()
        .getBaseUri()
        .getPath();
    basePath = basePath.endsWith("/") ?
        basePath.substring(0, basePath.length() - 1)
        : basePath;

    var pattern = basePath + endpoint;

    Timer.builder("http_server_duration")
        .tags(
            "http_target", pattern,
            "http_method", requestContext.getMethod(),
            "http_status_code", String.valueOf(responseContext.getStatus()),
            "service", metricsProperties.getServiceName()
        )
        .serviceLevelObjectives(
            metricsProperties.getDistribution()
        )
        .register(registry)
        .record(duration, TimeUnit.NANOSECONDS);
  }
}
