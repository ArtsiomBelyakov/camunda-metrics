package org.camunda.bpm.run.plugin.monitoring.slo;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class SloJaxRsRequestFilter implements ContainerRequestFilter {

  public static final String REQUEST_START_TIME = "REQUEST_START_TIME";

  @Override
  public void filter(ContainerRequestContext containerRequestContext) {
    long startTime = System.nanoTime();
    containerRequestContext.setProperty(REQUEST_START_TIME, startTime);
  }
}
